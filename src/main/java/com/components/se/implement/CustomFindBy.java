// Licensed to the Software Freedom Conservancy (SFC) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The SFC licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package com.components.se.implement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.openqa.selenium.support.How;

import com.components.se.enums.Until;

/**
 * Used to mark a field on a Page Object to indicate an alternative mechanism for locating
 * the element or a list of elements. Used in conjunction with
 * {@link org.openqa.selenium.support.PageFactory} this allows users to quickly and easily
 * create PageObjects.
 *
 * It can be used on a types as well, but will not be processed by default.
 *
 * <p>
 * You can either use this annotation by specifying both "how" and "using" or by
 * specifying one of the location strategies (eg: "id") with an appropriate value to use.
 * Both options will delegate down to the matching {@link org.openqa.selenium.By} methods
 * in By class.
 *
 * For example, these two annotations point to the same element:
 *
 * <pre>{@code
 * &#64;FindBy(id = "foobar") WebElement foobar;
 * &#64;FindBy(how = How.ID, using = "foobar") WebElement foobar;
 * }</pre>
 *
 * and these two annotations point to the same list of elements:
 *
 * <pre>{@code
 * &#64;FindBy(tagName = "a") List<WebElement> links;
 * &#64;FindBy(how = How.TAG_NAME, using = "a") List<WebElement> links;
 * }</pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.TYPE })
public @interface CustomFindBy {

    How how() default How.UNSET;

    String using() default "";

    String id() default "";

    String name() default "";

    String className() default "";

    String css() default "";

    String tagName() default "";

    String linkText() default "";

    String partialLinkText() default "";

    String xpath() default "";

    // customize part
    Until waitFor() default Until.Present;

    /**
     * The default value set to -1, but it will never be set as -1 If it is -1, we will
     * use the value of timeout parameter set in testng the context file.
     * @return int
     */
    int timeout() default -1;

}
