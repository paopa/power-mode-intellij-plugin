package _power.mode

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.TypedActionHandler

import java.awt.Point
import scala.jdk.CollectionConverters._

/**
  * Created by nyxos on 04.01.17.
  */
class MyTypedActionHandler(typedActionHandler: TypedActionHandler)
    extends TypedActionHandler
    with Power {

  def execute(editor: Editor, c: Char, dataContext: DataContext) {
    if (powerMode.isEnabled) {
      powerMode.increaseHeatup(dataContext = Some(dataContext))
      if (!powerMode.caretAction) {
        initializeAnimationByTypedAction(editor)
      }
    }
    try {
      typedActionHandler.execute(editor, c, dataContext)
    } catch {
      case x: IllegalStateException =>
        logger.info(x.getMessage, x)
      case x: IndexOutOfBoundsException =>
        logger.info(x.getMessage, x)
    }
  }

  def getEditorCaretPositions(editor: Editor): Seq[Point] = {
    editor.getCaretModel.getAllCarets.asScala.toList
      .map({ c =>
        Util.getCaretPosition(editor, c)
      })
      .filter(_.isSuccess)
      .map(_.get)
  }

  def initializeAnimationByTypedAction(editor: Editor): Unit = {

    val isActualEditor = Util.isActualEditor(editor)
    if (isActualEditor) {
      val positions = getEditorCaretPositions(editor)
      positions.foreach(pos => {
        powerMode.maybeElementOfPowerContainerManager.foreach(
          _.initializeAnimation(editor, pos))
      })
    }
  }
}
