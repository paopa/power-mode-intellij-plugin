/*
 * Copyright 2015 Baptiste Mesta
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
package _power.mode

import com.intellij.openapi.options.ConfigurableBase
import power.mode.config.ui.PowerModeConfigurableUI

/**
 * @author Baptiste Mesta
 */
class PowerModeConfigurable() extends ConfigurableBase[PowerModeConfigurableUI, PowerMode](
  "power.mode.II-X",
  "Power Mode II-X",
  "power.mode.II-X") {
  val settings: PowerMode = PowerMode.getInstance

  protected def getSettings: PowerMode = {
    if (settings == null) {
      throw new IllegalStateException("power mode is null")
    }
    settings
  }

  protected def createUi: PowerModeConfigurableUI = {
    new PowerModeConfigurableUI(settings)
  }
}
