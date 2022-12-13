#!/usr/bin/python
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

import unittest
import ./backend/api_calls


class FooTest(unittest.TestCase):

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def testBar(self):
        pass


if __name__ == '__main__':
    unittest.main()
#!/bin/python2.7

import mock
import unittest

import api_calls


class FakeFirebase(object):

  def get(self, path, item):
    return None

  def put(self, path, item, data):
    return None

  def patch(self, path, data):
    return None


class TestApiCalls(unittest.TestCase):

  def setUp(self):
    self.db = FakeFirebase()
    self.mdb = mock.create_autospec(FakeFirebase)

  def testValidateInputs(self):
    request = {}
    api_calls.ValidateInputs(request, self.db, [], [])

    # If we have a fooId, the value must start foo-
    request = {'gunId': 'gunFoo'}
    self.assertRaises(api_calls.InvalidInputError, api_calls.ValidateInputs, request, self.db, [], [])
    request = {'gunId': 'gun-Foo'}
    api_calls.ValidateInputs(request, self.db, [], [])

  def testRegister(self):
    """Register does a get/put and fails when the user already exists."""
    self.mdb.get.return_value = None
    api_calls.Register({'userToken': 'foo'}, self.mdb)
    self.mdb.get.assert_called_once_with('/users/foo', 'a')
    self.mdb.put.assert_called_once_with('/users', 'foo', {'a': True})

    self.mdb.get.return_value = True
    self.assertRaises(api_calls.InvalidInputError, api_calls.Register, {'userToken': 'foo'}, self.mdb)

  def testCreateGame(self):
    pass

  def testUpdateGame(self):
    pass

  def testCreateGroup(self):
    pass

  def testUpdateGroup(self):
    pass

  def testCreatePlayer(self):
    pass

  def testAddGun(self):
    pass

  def testAssignGun(self):
    pass

  def testUpdatePlayer(self):
    pass

  def testAddMission(self):
    pass

  def testUpdateMission(self):
    pass

  def testCreateChatRoom(self):
    pass

  def testAddPlayerToChat(self):
    pass

  def testSendChatMessage(self):
    pass

  def testAddRewardCategory(self):
    pass

  def testUpdateRewardCategory(self):
    pass

  def testAddReward(self):
    pass

  def testClaimReward(self):
    pass


if __name__ == '__main__':
  unittest.main()


# vim:ts=2:sw=2:expandtab
