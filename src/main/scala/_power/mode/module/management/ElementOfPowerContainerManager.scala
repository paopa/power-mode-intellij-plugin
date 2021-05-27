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
package _power.mode.module.management

import _power.mode.module.sound.PowerSound
import _power.mode.{Power, PowerMode, Util}
import com.intellij.openapi.actionSystem.{DataConstants, DataContext, PlatformDataKeys}
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.{EditorFactoryAdapter, EditorFactoryEvent}
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project

import java.awt._
import javax.swing._
import scala.collection.mutable
import scala.util.{Failure, Success, Try}

/**
  * @author Baptiste Mesta
  */
class ElementOfPowerContainerManager extends EditorFactoryAdapter with Power {
  def ForceTry[X](f: => X): Try[X] = {
    try {
      Success(f).filter(_ != null)
    } catch {
      case e: Throwable =>
        e.printStackTrace()
        logger.error("error doing ForceTry", e)
        Failure(e)
    }
  }

  val elementsOfPowerContainers =
    mutable.Map.empty[Editor, ElementOfPowerContainer]

  private lazy val triedSound: Try[PowerSound] =
    powerMode.mediaPlayerExists.flatMap { _ =>
      Try {
        new PowerSound(powerMode.soundsFolder, powerMode.valueFactor)
      }
    }
  lazy val sound = triedSound

  def showIndicator(dataContext: DataContext) {
    if (powerMode.powerIndicatorEnabled && powerMode.isEnabled) {
      val maybeProject: Seq[Project] = Seq(ForceTry {
        dataContext.getData(DataConstants.PROJECT)
      }, ForceTry {
        dataContext.getData(PlatformDataKeys.PROJECT_CONTEXT)
      }).toStream.flatMap(o =>
        o.toOption.flatMap(Option(_)).map(_.asInstanceOf[Project]))
      maybeProject.headOption.foreach(p => {
        val textEditor: Editor =
          FileEditorManager.getInstance(p).getSelectedTextEditor
        SwingUtilities.invokeLater(() => {
          elementsOfPowerContainers
            .get(textEditor)
            .foreach(_.addPowerIndicator())
        })
      })
    }
  }

  val elementsOfPowerUpdateThread = new Thread(new Runnable() {
    def run {
      while (true) {
        try {
          if (powerMode != null) {
            powerMode.reduceHeatup
            updateSound
            updateContainers
            try {
              Thread.sleep(1000 / powerMode.frameRate)
            } catch {
              case ignored: InterruptedException => {}
            }
          }
        } catch {
          case e :Throwable => PowerMode.logger.error(e.getMessage, e)
        }
      }
    }

    def updateContainers: Unit = {
      elementsOfPowerContainers.values.foreach(_.updateElementsOfPower())
    }

    var soundErrorLogged = System.currentTimeMillis()

    def updateSound: Unit = {
      try {
        if (powerMode.isEnabled &&
            powerMode.soundsFolder.exists(f => f.exists() && f.isDirectory)
            && powerMode.isSoundsPlaying) {
          if (sound.isFailure && soundErrorLogged + 5000 < System
                .currentTimeMillis()) {
            logger.error(sound.failed.get.getMessage, sound.failed.get)
            soundErrorLogged += 1
          }
          sound.foreach(_.play())
        } else {
          sound.foreach(_.stop())
        }
        sound.foreach(_.setVolume(powerMode.valueFactor))
      } catch {
        case e: Throwable =>
          logger.error(e.getMessage, e)
      }
    }
  })
  elementsOfPowerUpdateThread.start()

  override def editorCreated(event: EditorFactoryEvent) {
    val editor: Editor = event.getEditor
    val isActualEditor = Try {
      Util.isActualEditor(editor)
    }.getOrElse(false)
    if (isActualEditor) {
      elementsOfPowerContainers
        .put(editor, new ElementOfPowerContainer(editor))
    }
  }

  override def editorReleased(event: EditorFactoryEvent) {
    elementsOfPowerContainers.remove(event.getEditor)
  }

  def initializeAnimation(editor: Editor, pos: Point) {
    if (powerMode.isEnabled) {
      SwingUtilities.invokeLater(new Runnable() {
        def run {
          initializeInUI(editor, pos)
        }
      })
    }
  }

  private def initializeInUI(editor: Editor, pos: Point) {
    elementsOfPowerContainers.get(editor).foreach(_.initializeAnimation(pos))
  }

  def dispose {
    elementsOfPowerUpdateThread.interrupt()
    elementsOfPowerContainers.clear
  }
}
