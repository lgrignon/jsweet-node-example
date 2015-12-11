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
package app.controllers;

import static def.jquery.Globals.$;
import static jsweet.dom.Globals.console;
import static jsweet.dom.Globals.localStorage;
import static jsweet.util.Globals.function;

import java.util.function.BiFunction;
import java.util.function.Function;

import app.services.MessageService;
import jsweet.lang.Array;
import jsweet.lang.JSON;
import model.Message;
import model.User;
import def.angular_material.ng.material.ISimpleToastPreset;
import def.angular_material.ng.material.IToastService;
import def.angularjs.ng.IPromise.SuccessCallback;
import def.angularjs.ng.IScope;
import def.jquery.JQueryEventObject;
import def.socket_io.socketio.Socket;

/**
 * Controller for the main chat view
 * 
 * @author Louis Grignon
 *
 */
public class ChatController extends AbstractController {
	private Socket socket;
	public User user;
	public Array<Message> messages = new Array<Message>();
	private IScope $scope;

	static String[] $inject = { "$scope", "$mdToast", "socket", "messageService" };
	private MessageService messageService;

	private IToastService $mdToast;

	ChatController(IScope $scope, IToastService $mdToast, Socket ngSocket, MessageService messageService) {
		this.$scope = $scope;
		this.$mdToast = $mdToast;
		this.socket = ngSocket;
		this.messageService = messageService;
		this.init();

		this.socket.emit("request:loggedUser");
		this.socket.emit("request:lastMessages");
		this.socket.emit("request:loggedUsers");
	}

	private void init() {
		Function<IScope, Array<Message>> watchMessages = ($scope) -> this.messages;
		this.$scope.$watchCollection(watchMessages, this::onMessagesChanged);

		$("#messageForm").submit(this::sendMessage);

		Function<Message, Void> onNewMsg = this::onMessageReceived;
		this.socket.on("newmsg", function(onNewMsg));

		Function<Array<Message>, Void> onMessagesLoaded = this::initMessages;
		this.socket.on("response:lastMessages", function(onMessagesLoaded));

		BiFunction<User, Number, Void> onUserConnected = this::onUserConnected;
		this.socket.on("newuser", function(onUserConnected));

		BiFunction<User, Number, Void> onUserDisconnected = this::onUserDisconnected;
		this.socket.on("userdisconnect", function(onUserDisconnected));

		Function<User, Void> onUserLogged = this::initUser;
		this.socket.on("response:loggedUser", function(onUserLogged));

		$("#newMsg").focus();
	}

	private Void onMessagesChanged(Array<Message> oldVal, Array<Message> newVal, IScope $scope) {
		console.log("messages changed - scrolling to end!");

		$("#messageList").scrollTop($("#messageList").get(0).scrollHeight + 100);

		return null;
	}

	private Void initUser(User user) {
		this.user = new User(user.name, user.ip, user.id);

		if (localStorage.getItem("IM_USER") == null) {
			localStorage.setItem("IM_USER", JSON.stringify(this.user));
		}

		return null;
	}

	private Void refreshConnectedCount(Number usersCount) {
		$("#usersCount").text("" + usersCount);
		return null;
	}

	private Void initMessages(Array<Message> messages) {
		console.log("last Messages", messages);

		messages.forEach(message -> {
			onMessageReceived(new Message(message.user, message.text));
		});

		this.updateUI($scope);

		return null;
	}

	private Void sendMessage(JQueryEventObject event) {
		event.preventDefault();
		this.messageService.sendMessage(user, "" + $("#newMsg").val());
		$("#newMsg").val("").focus();

		return null;
	}

	private Void onMessageReceived(Message message) {
		console.log("message received: ", message);

		this.messages.push(new Message(message.user, message.text));

		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Void onUserConnected(User user, Number connectedCount) {

		ISimpleToastPreset toast = $mdToast.simple() //
				.content(user.name + " is connected") //
				.action("x") //
				.highlightAction(true) //
				.position("bottom right");

		$mdToast.show(toast) //
				.then((SuccessCallback) (response) -> {
					if (response == "x") {
						$mdToast.hide();
					}
					return null;
				});

		this.refreshConnectedCount(connectedCount);

		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Void onUserDisconnected(User user, Number connectedCount) {
		ISimpleToastPreset toast = $mdToast.simple() //
				.content(user.name + " left") //
				.action("x") //
				.highlightAction(true) //
				.position("bottom right");

		$mdToast.show(toast) //
				.then((SuccessCallback) (response) -> {
					if (response == "x") {
						$mdToast.hide();
					}
					return null;
				});

		this.refreshConnectedCount(connectedCount);

		return null;
	}
}
