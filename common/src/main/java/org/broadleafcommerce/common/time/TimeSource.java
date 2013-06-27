/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.common.time;

/**
 * Provides an abstraction from the current system time.
 * Certain aspects of Broadleaf can be run in a mode that allows the end user to override the
 * current time.
 *
 * A convenient example of this is when previewing content.   An approver may want to view
 * the site as it would appear on a particular date or time.
 *
 * See BroadleafProcessURLFilter for example usage of this construct.
 */
public interface TimeSource {

    long timeInMillis();
}