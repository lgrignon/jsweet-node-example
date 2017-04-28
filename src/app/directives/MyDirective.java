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
package app.directives;

import static jsweet.util.Lang.union;

import def.angularjs.ng.IAttributes;
import def.angularjs.ng.IAugmentedJQuery;
import def.angularjs.ng.IDirective;
import def.angularjs.ng.IDirectiveLinkFn;
import def.angularjs.ng.IDirectivePrePost;
import def.angularjs.ng.IScope;
import def.angularjs.ng.ITranscludeFunction;
import def.js.Function;
import jsweet.util.union.Union;

/**
 * Demo directive, unused for now
 * 
 * @author Louis Grignon
 *
 */
public class MyDirective extends IDirective {

	Union<String, Function> template;
	String restrict;
	Union<IDirectiveLinkFn, IDirectivePrePost> link;

	public MyDirective() {
		template = union("<div></div>");
		restrict = "E";

		link = union((IDirectiveLinkFn) this::linkElement);
	}

	public void linkElement(IScope scope, IAugmentedJQuery element, IAttributes instanceAttributes, Object controller, ITranscludeFunction transclude) {
		element.text("this is the MyDirective directive");
	}
	
	@Override
	public IDirectivePrePost compile(IAugmentedJQuery templateElement, IAttributes templateAttributes, ITranscludeFunction transclude) {
		return null;
	}
}
