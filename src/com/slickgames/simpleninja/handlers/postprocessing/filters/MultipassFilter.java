/*******************************************************************************
 * Copyright 2012 bmanuel
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.slickgames.simpleninja.handlers.postprocessing.filters;

import com.slickgames.simpleninja.handlers.postprocessing.utils.PingPongBuffer;

/** The base class for any multi-pass filter. Usually a multi-pass filter will make use of one or more single-pass filters,
 * promoting composition over inheritance. */
public abstract class MultipassFilter {
    public abstract void rebind();

    public abstract void render(PingPongBuffer srcdest);
}
