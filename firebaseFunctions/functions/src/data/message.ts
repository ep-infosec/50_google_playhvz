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

export const COLLECTION_PATH = "messages";
export const FIELD__SENDER_ID = "senderId";
export const FIELD__TIMESTAMP = "timestamp";
export const FIELD__MESSAGE = "message";

export function create(senderId: string, timestamp: any, message: string): { [key: string]: any; } {
  return {
    [FIELD__SENDER_ID]: senderId,
    [FIELD__TIMESTAMP]: timestamp,
    [FIELD__MESSAGE]: message
  };
}


