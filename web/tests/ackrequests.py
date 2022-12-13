#?/usr/bin/python
#
# Copyright 2017 Google Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

"""TODO: High-level file comment."""

import sys


def main(argv):
    pass


if __name__ == '__main__':
    main(sys.argv)
import setup
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys

# Sign in as a normal human.
driver = setup.MakeDriver(user="zella")

driver.Click([[By.NAME, 'close-notification']])

# Create a new chat room
driver.DrawerMenuClick('New chat')
driver.FindElement([[By.ID, 'chatName']])
driver.SendKeys([[By.ID, 'chatName'], [By.TAG_NAME, 'input']], "Expelliarmus")
driver.Click([[By.ID, 'settingsForm'], [By.ID, 'done']])

# Add two people to chat
driver.Click([[By.NAME, 'chat-card'], [By.NAME, 'chat-info-Expelliarmus']])
driver.Click([[By.NAME, 'chat-card'], [By.NAME, 'chat-drawer-add']])
driver.SendKeys([[By.TAG_NAME, 'ghvz-chat-page'], [By.TAG_NAME, 'ghvz-player-dialog'], [By.TAG_NAME, 'input']], 'MoldaviTheMoldavish')
driver.SendKeys([[By.TAG_NAME, 'ghvz-chat-page'], [By.TAG_NAME, 'ghvz-player-dialog'], [By.TAG_NAME, 'input']], Keys.RETURN)

driver.Click([[By.NAME, 'chat-card'], [By.NAME, 'chat-info-Expelliarmus']])
driver.Click([[By.NAME, 'chat-card'], [By.NAME, 'chat-info-Expelliarmus']])
driver.Click([[By.NAME, 'chat-card'], [By.NAME, 'chat-drawer-add']])
driver.SendKeys([[By.TAG_NAME, 'ghvz-chat-page'], [By.TAG_NAME, 'ghvz-player-dialog'], [By.TAG_NAME, 'input']], 'Drackan')
driver.SendKeys([[By.TAG_NAME, 'ghvz-chat-page'], [By.TAG_NAME, 'ghvz-player-dialog'], [By.TAG_NAME, 'input']], Keys.RETURN)
driver.Click([[By.NAME, 'chat-card'], [By.NAME, 'chat-info-Expelliarmus']])

driver.Click([[By.NAME, 'chat-card'], [By.NAME, 'chat-info-Expelliarmus']])
driver.ExpectContains([[By.TAG_NAME, 'ghvz-chat-page'], [By.NAME, 'num-players']], '3 members')
driver.Click([[By.NAME, 'chat-card'], [By.NAME, 'chat-info-Expelliarmus']])

# Send an ack to everyone
driver.SendKeys([
  [By.NAME, 'chat-card'], 
  [By.NAME, 'input-Expelliarmus'], 
  [By.TAG_NAME, 'textarea']], '@?all Why are we sorcerers? What does this have to do with zombies?')
driver.Click([[By.NAME, 'chat-card'], [By.NAME, 'submit-Expelliarmus']])

# See it appear
driver.ExpectContains([[By.NAME, 'chat-card'], [By.CLASS_NAME, 'message-from-me']], 
		'@?all Why are we sorcerers? What does this have to do with zombies?')
driver.ExpectContains([[By.NAME, 'chat-card'], 
	[By.NAME, 'request-message-Why are we sorcerers? What does this have to do with zombies?']], 
	"Why are we sorcerers? What does this have to do with zombies?")
driver.ExpectContains([[By.NAME, 'chat-card'], 
	[By.NAME, 'request-message-Why are we sorcerers? What does this have to do with zombies?']], 
	"0/2")
driver.ExpectContains([
	[By.NAME, 'chat-card'], 
	[By.NAME, 'request-message-Why are we sorcerers? What does this have to do with zombies?'],
	[By.NAME, 'not-responded']], 
	"MoldaviTheMoldavish, Drackan")
driver.FindElement(
	[[By.NAME, 'preview-ZellaTheUltimate: Why are we sorcerers? What does this have to do with zombies?']],
	should_exist=False) #Shouldn't see your own notification

# moldavi sees the ack
driver.SwitchUser('moldavi')
driver.Click([[By.TAG_NAME, 'ghvz-notification-preview'], [By.NAME, 'close-notification']]) # Close "first mission" notification
driver.FindElement([[By.NAME, 'preview-ZellaTheUltimate: Why are we sorcerers? What does this have to do with zombies?']])
driver.DrawerMenuClick('Expelliarmus')
driver.ExpectContains([[By.NAME, 'chat-card'], [By.CLASS_NAME, 'message-from-other']], 
		'@?all Why are we sorcerers? What does this have to do with zombies?')
driver.ExpectContains([[By.NAME, 'chat-card'], 
	[By.NAME, 'received-request-ack-Why are we sorcerers? What does this have to do with zombies?']], 
	"Why are we sorcerers? What does this have to do with zombies?")

#Sees ack, clicks through to Zella's Profile
driver.Click([[By.NAME, 'chat-card'], 
	[By.NAME, 'received-request-ack-Why are we sorcerers? What does this have to do with zombies?'],
	[By.TAG_NAME, 'a']])
driver.ExpectContains([[By.NAME, 'player-name']], 'ZellaTheUltimate')
driver.DrawerMenuClick('Expelliarmus')

# Acks the message - should get error since message is empty
driver.Click([[By.NAME, 'chat-card'], 
	[By.NAME, 'received-request-ack-Why are we sorcerers? What does this have to do with zombies?'],
	[By.NAME, 'ack-request-button']])
# TODO(aliengirl): Come back and change this once fake & remote deal w. empty mesages the same way
driver.Optional(lambda: driver.DismissAlert()) 

# Reply
driver.SendKeys([[By.NAME, 'chat-card'], 
	[By.NAME, 'received-request-ack-Why are we sorcerers? What does this have to do with zombies?'],
	[By.TAG_NAME, 'input']], 'there were those inferi things in Harry Potter')
driver.Click([[By.NAME, 'chat-card'], 
	[By.NAME, 'received-request-ack-Why are we sorcerers? What does this have to do with zombies?'],
	[By.NAME, 'ack-request-button']])
driver.FindElement([[By.NAME, 'chat-card'], 
	[By.NAME, 'received-request-ack-Why are we sorcerers? What does this have to do with zombies?']], 
	should_exist=False)

# Zella sees the response
driver.SwitchUser('zella')
driver.ExpectContains([[By.NAME, 'chat-card'], 
	[By.NAME, 'request-message-Why are we sorcerers? What does this have to do with zombies?']], 
	"1/2")
driver.ExpectContains([
	[By.NAME, 'chat-card'], 
	[By.NAME, 'request-message-Why are we sorcerers? What does this have to do with zombies?'],
	[By.NAME, 'not-responded']], 
	"Drackan")
driver.ExpectContains([
	[By.NAME, 'chat-card'], 
	[By.NAME, 'request-message-Why are we sorcerers? What does this have to do with zombies?'],
	[By.NAME, 'request-response-there were those inferi things in Harry Potter']], 
	"there were those inferi things in Harry Potter")
driver.ExpectContains([
	[By.NAME, 'chat-card'], 
	[By.NAME, 'request-message-Why are we sorcerers? What does this have to do with zombies?'],
	[By.NAME, 'request-response-there were those inferi things in Harry Potter']], 
	"MoldaviTheMoldavish")

# Drake replies too
driver.SwitchUser('drake')
driver.DrawerMenuClick('Expelliarmus')
driver.SendKeys([[By.NAME, 'chat-card'], 
	[By.NAME, 'received-request-ack-Why are we sorcerers? What does this have to do with zombies?'],
	[By.TAG_NAME, 'input']], 'necromancy is a type of sorcery, right?')
driver.Click([[By.NAME, 'chat-card'], 
	[By.NAME, 'received-request-ack-Why are we sorcerers? What does this have to do with zombies?'],
	[By.NAME, 'ack-request-button']])
driver.FindElement([[By.NAME, 'chat-card'], 
	[By.NAME, 'received-request-ack-Why are we sorcerers? What does this have to do with zombies?']], 
	should_exist=False)


# Zella sees that he has acked it too
driver.SwitchUser('zella')
driver.ExpectContains([[By.NAME, 'chat-card'], 
	[By.NAME, 'request-message-Why are we sorcerers? What does this have to do with zombies?']], 
	"2/2")
driver.FindElement([
	[By.NAME, 'chat-card'], 
	[By.NAME, 'request-message-Why are we sorcerers? What does this have to do with zombies?'],
	[By.NAME, 'not-responded']], 
	should_exist=False)
driver.ExpectContains([
	[By.NAME, 'chat-card'], 
	[By.NAME, 'request-message-Why are we sorcerers? What does this have to do with zombies?'],
	[By.NAME, 'request-response-there were those inferi things in Harry Potter']], 
	"MoldaviTheMoldavish")
driver.ExpectContains([
	[By.NAME, 'chat-card'], 
	[By.NAME, 'request-message-Why are we sorcerers? What does this have to do with zombies?'],
	[By.NAME, 'request-response-necromancy is a type of sorcery, right?']], 
	"Drackan")
driver.ExpectContains([
	[By.NAME, 'chat-card'], 
	[By.NAME, 'request-message-Why are we sorcerers? What does this have to do with zombies?'],
	[By.NAME, 'request-response-necromancy is a type of sorcery, right?']], 
	"necromancy is a type of sorcery, right?")


# Zella clicks the dismiss button
driver.Click([
	[By.NAME, 'chat-card'], 
	[By.NAME, 'request-message-Why are we sorcerers? What does this have to do with zombies?'],
	[By.NAME, 'dismiss-button']])
driver.FindElement([[By.NAME, 'chat-card'], 
	[By.NAME, 'request-message-Why are we sorcerers? What does this have to do with zombies?']], 
	should_exist=False)


################### moldavi's turn to send a message (just to one person this time) ###################
driver.SwitchUser('moldavi')
driver.DrawerMenuClick('Expelliarmus')
driver.SendKeys([
  [By.NAME, 'chat-card'], 
  [By.NAME, 'input-Expelliarmus'], 
  [By.TAG_NAME, 'textarea']], '@?ZellaTheUltimate We should totally do wizard duels with zombies?')
driver.Click([[By.NAME, 'chat-card'], [By.NAME, 'submit-Expelliarmus']])

# See it appear
driver.ExpectContains([[By.NAME, 'chat-card'], [By.CLASS_NAME, 'message-from-me']], 
		'@?ZellaTheUltimate We should totally do wizard duels with zombies?')
driver.ExpectContains([[By.NAME, 'chat-card'], 
	[By.NAME, 'request-message-We should totally do wizard duels with zombies?']], 
	"We should totally do wizard duels with zombies?")
driver.ExpectContains([[By.NAME, 'chat-card'], 
	[By.NAME, 'request-message-We should totally do wizard duels with zombies?']], 
	"0/1")
driver.ExpectContains([
	[By.NAME, 'chat-card'], 
	[By.NAME, 'request-message-We should totally do wizard duels with zombies?'],
	[By.NAME, 'not-responded']], 
	"ZellaTheUltimate")

# Zella sees the ack
driver.SwitchUser('zella')
driver.FindElement([[By.NAME, 'preview-MoldaviTheMoldavish: We should totally do wizard duels with zombies?']])
driver.DrawerMenuClick('Expelliarmus')
driver.ExpectContains([[By.NAME, 'chat-card'], [By.CLASS_NAME, 'message-from-other']], 
		'@?ZellaTheUltimate We should totally do wizard duels with zombies?')
driver.ExpectContains([[By.NAME, 'chat-card'], 
	[By.NAME, 'received-request-ack-We should totally do wizard duels with zombies?']], 
	"We should totally do wizard duels with zombies?")

# Zella responds
driver.SendKeys([[By.NAME, 'chat-card'], 
	[By.NAME, 'received-request-ack-We should totally do wizard duels with zombies?'],
	[By.TAG_NAME, 'input']], 'awww, yeah!')
driver.Click([[By.NAME, 'chat-card'], 
	[By.NAME, 'received-request-ack-We should totally do wizard duels with zombies?'],
	[By.NAME, 'ack-request-button']])
driver.FindElement([[By.NAME, 'chat-card'], 
	[By.NAME, 'received-request-ack-We should totally do wizard duels with zombies?']], 
	should_exist=False)

# moldavi sees the response
driver.SwitchUser('moldavi')
driver.DrawerMenuClick('Expelliarmus')
driver.ExpectContains([[By.NAME, 'chat-card'], 
	[By.NAME, 'request-message-We should totally do wizard duels with zombies?']], 
	"1/1")
driver.FindElement([
	[By.NAME, 'chat-card'], 
	[By.NAME, 'request-message-We should totally do wizard duels with zombies?'],
	[By.NAME, 'not-responded']], should_exist=False)
driver.ExpectContains([
	[By.NAME, 'chat-card'], 
	[By.NAME, 'request-message-We should totally do wizard duels with zombies?'],
	[By.NAME, 'request-response-awww, yeah!']], 
	"ZellaTheUltimate")
driver.ExpectContains([
	[By.NAME, 'chat-card'], 
	[By.NAME, 'request-message-We should totally do wizard duels with zombies?'],
	[By.NAME, 'request-response-awww, yeah!']], 
	"awww, yeah!")

driver.Quit()



