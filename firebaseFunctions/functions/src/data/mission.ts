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

export const COLLECTION_PATH = "missions";
export const FIELD__GROUP_ID = "associatedGroupId";
export const FIELD__NAME = "name";
export const FIELD__START_TIME = "startTime";
export const FIELD__END_TIME = "endTime";
export const FIELD__DETAILS = "details";
export const FIELD__ALLEGIANCE_FILTER = "allegianceFilter";

export function create(
  groupId: string,
  name: string,
  startTime: number,
  endTime: number,
  details: string,
  allegianceFilter: string)
: { [key: string]: any; } {
  return {
    [FIELD__GROUP_ID]: groupId,
    [FIELD__NAME]: name,
    [FIELD__START_TIME]: startTime,
    [FIELD__END_TIME]: endTime,
    [FIELD__DETAILS]: details,
    [FIELD__ALLEGIANCE_FILTER]: allegianceFilter
  };
}
