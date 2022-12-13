/*
 * Copyright 2020 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import * as functions from 'firebase-functions';

import * as Defaults from '../data/defaults';
import * as Game from '../data/game';
import * as GeneralUtils from '../utils/generalutils';
import * as Group from '../data/group';
import * as GroupUtils from '../utils/grouputils';
import * as Player from '../data/player';
import * as PlayerUtils from '../utils/playerutils';
import * as RewardUtils from '../utils/rewardutils';
import * as Stat from '../data/stat';
import * as StatUtils from '../utils/statutils';


/**
 * Function to create a new game and all the internals required.
 */
export async function createGame(
  db: any,
  uid: string,
  gameName: string,
  startTime: number,
  endTime: number
): Promise<string> {
  const gameQuery = await db.collection(Game.COLLECTION_PATH).where(Game.FIELD__NAME, "==", gameName).get();
  if (!gameQuery.empty) {
    throw new functions.https.HttpsError('already-exists', 'A game with the given name already exists');
  }
  GeneralUtils.verifyStartEndTime(startTime, endTime)
  const gameData = Game.create(uid, gameName, startTime, endTime)
  const gameRef = await db.collection(Game.COLLECTION_PATH).add(gameData)
  const gameId = gameRef.id

  // Create managed properties
  await GroupUtils.createManagedGroups(db, gameId);
  await RewardUtils.createManagedRewards(db, gameId)

  const adminGroupQuery = await db.collection(Game.COLLECTION_PATH)
      .doc(gameId)
      .collection(Group.COLLECTION_PATH)
      .where(Group.FIELD__MANAGED, "==", true)
      .where(Group.FIELD__NAME, "==", Defaults.gameAdminChatName)
      .get()
  if (!adminGroupQuery.empty && adminGroupQuery.docs.length === 1) {
    const adminGroupId = adminGroupQuery.docs[0].id
    await gameRef.update({
      [Game.FIELD__ADMIN_GROUP_ID]: adminGroupId
    })
  }

  // Create admin on call player
  const figureheadPlayerData = Player.create("", Defaults.FIGUREHEAD_ADMIN_NAME);
  const figureheadPlayerRef = await db.collection(Game.COLLECTION_PATH)
      .doc(gameId)
      .collection(Player.COLLECTION_PATH)
      .add(figureheadPlayerData);

  await GroupUtils.addPlayerToManagedGroups(db, gameId, figureheadPlayerRef, /* ignoreAllegiance= */ true)
  await gameRef.update({
    [Game.FIELD__FIGUREHEAD_ADMIN_PLAYER_ACCOUNT]: figureheadPlayerRef.id
  })
  return gameId;
}

/**
 * Function to update the update-able fields of a game.
 */
export async function updateGame(
  db: any,
  gameId: string,
  adminOnCallPlayerId: string,
  startTime: number,
  endTime: number
) {
  GeneralUtils.verifyStartEndTime(startTime, endTime)
  const gameRef = db.collection(Game.COLLECTION_PATH).doc(gameId);
  const gameData = (await gameRef.get()).data()
  if (gameData[Game.FIELD__START_TIME] !== startTime || gameData[Game.FIELD__END_TIME] !== endTime) {
    await StatUtils.invalidateGameStats(db, gameId)
  }
  await gameRef.update({
      [Game.FIELD__ADMIN_ON_CALL_PLAYER_ID]: adminOnCallPlayerId,
      [Game.FIELD__START_TIME]: startTime,
      [Game.FIELD__END_TIME]: endTime
    });
}


/**
 * Function that throws an error if a game with the given name doesn't exist.
 *
 * @returns The game's id, assuming it exists.
 */
export async function checkGameExists(
  db: any,
  gameName: string
): Promise<string> {
  const querySnapshot = await db.collection(Game.COLLECTION_PATH)
    .where(Game.FIELD__NAME, "==", gameName)
    .get();
  if (querySnapshot.empty || querySnapshot.docs.length > 1) {
    throw new functions.https.HttpsError('failed-precondition', 'No game with given name exists.');
  }
  return querySnapshot.docs[0].id
}

/**
 * Function that adds a player to a game.
 *
 * @returns The game's id, assuming it exists.
 */
export async function joinGame(
  db: any,
  uid: string,
  gameName: string,
  playerName: string
): Promise<string> {
  const gameId = await checkGameExists(db, gameName)
  const userPlayerQuerySnapshot = await PlayerUtils.getUsersPlayersQuery(db, uid, gameId).get();
  if (!userPlayerQuerySnapshot.empty) {
    throw new functions.https.HttpsError('failed-precondition', 'User is already a player in this game.');
  }

  const playerNameQuerySnapshot = await PlayerUtils.getPlayersWithNameQuery(db, gameId, playerName).get();
  if (!playerNameQuerySnapshot.empty) {
      throw new functions.https.HttpsError('failed-precondition', 'Player name already taken.');
  }

  const playerData = Player.create(uid, playerName);
  const playerDocRef = (await db.collection(Game.COLLECTION_PATH)
    .doc(gameId)
    .collection(Player.COLLECTION_PATH)
    .add(playerData));

  await GroupUtils.addPlayerToManagedGroups(db, gameId, playerDocRef, /* ignoreAllegiance= */ false)
  return gameId
}

/**
 * Initiate a recursive delete of the game, deleting all documents at a given path.
 *
 * The calling user must be authenticated and have the custom "admin" attribute
 * set to true on the auth token.
 *
 * This delete is NOT an atomic operation and it's possible
 * that it may fail after only deleting some documents.
 *
 * @param {string} data.path the document or collection path to delete.
 */
export async function deleteGame(
   db: any,
   uid: string,
   gameId: string
) {
    console.log(`User ${uid} has requested to delete game ${gameId}`);
    // Run a recursive delete on the game document path.
    const gameRef = db.collection(Game.COLLECTION_PATH).doc(gameId);
    await gameRef.listCollections().then(async (collections: any) => {
      for (const collection of collections) {
        await GeneralUtils.deleteCollection(db, collection)
      }
      await gameRef.delete()
    });
}


/**
 * Function that returns the latest game stats to the player. If stats are out of date then they
 * will be freshly calculated.
 *
 * @returns The game's stats as a JSON
 */
export async function getGameStats(
  db: any,
  gameId: string
): Promise<{ [key: string]: any; }> {
  const gameSnapshot = await db.collection(Game.COLLECTION_PATH).doc(gameId).get()
  const gameData = gameSnapshot.data()
  if (gameData === undefined) {
    throw new functions.https.HttpsError('failed-precondition', 'Error getting game data.');
  }

  if (!gameData[Game.FIELD__STAT_ID]) {
    // Stat object doesn't exist yet, create it.
    return StatUtils.createGameStats(db, gameId, gameData)
  }
  const statId = gameData[Game.FIELD__STAT_ID]
  const statSnapshot = await db.collection(Game.COLLECTION_PATH)
    .doc(gameId)
    .collection(Stat.COLLECTION_PATH)
    .doc(statId)
    .get()
  const statData = statSnapshot.data()
  if (statData === undefined) {
    throw new functions.https.HttpsError('failed-precondition', 'Error getting game stats.');
  }
  if (statData[Stat.FIELD__IS_OUT_OF_DATE]) {
    // Stats are out of date, recalculate them.
    return StatUtils.updateGameStats(db, gameId, gameData)
  }
  return Stat.formattedForReturn(statData)
}
