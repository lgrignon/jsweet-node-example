/* 
 * Copyright (C) 2015 Louis Grignon <louis.grignon@gmail.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package model;

/**
 * A chat's message
 * 
 * @author Louis Grignon
 *
 */
public class Message {
	public String text;
	public User user;

	public Message(User user, String text) {
		this.user = user;
		this.text = text;
	}

	@Override
	public String toString() {
		return "'" + text + "' by " + user;
	}
}
