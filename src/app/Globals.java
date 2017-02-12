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
package app;

import static def.angularjs.Globals.angular;
import static def.js.Globals.console;
import static jsweet.util.Globals.array;
import static jsweet.util.Globals.function;
import static jsweet.util.Globals.union;

import java.util.function.Consumer;

import app.controllers.ChatController;
import app.controllers.LoginController;
import app.directives.MyDirective;
import app.services.MessageService;
import def.angularjs.ng.route.IRoute;
import def.angularjs.ng.route.IRouteProvider;
import def.js.Array;

/**
 * The following script initializes angular module and configures routing
 * 
 * @author Louis Grignon
 *
 */
public class Globals {

	public static void main(String[] args) {

		// Create and register modules
		Array<String> modules = new Array<>();
		modules.push("ngRoute");
		modules.push("ngLocale");
		modules.push("ngAnimate");
		modules.push("ngMaterial");
		modules.push("socket-io");

		angular.module("app", array(modules));

		console.log("register services");
		angular.module("app").directive("myDirective", (__) -> {
			return new MyDirective();
		});

		console.log("register services");
		angular.module("app").service("messageService", function(MessageService.class));

		console.log("register controllers");
		angular.module("app").controller("app.controllers.LoginController", function(LoginController.class));
		angular.module("app").controller("app.controllers.ChatController", function(ChatController.class));

		// Routing
		console.log("configuring routing");
		angular.module("app").config(new Object[] { "$routeProvider", (Consumer<IRouteProvider>) (IRouteProvider $routeProvider) -> {
			$routeProvider.when("/", new IRoute() {
				{
					templateUrl = union("views/login.html");
					controller = union("app.controllers.LoginController as ctrl");
				}
			}).when("/chatroom", new IRoute() {
				{
					templateUrl = union("views/chatroom.html");
					controller = union("app.controllers.ChatController as ctrl");
				}
			}).otherwise(new IRoute() {
				{
					redirectTo = union("/");
				}
			});
		} });

		console.log("app configured");
	}
}
