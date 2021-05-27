package _power.mode.module.element

import _power.mode.module.ElementOfPower
import _power.mode.{ImageUtil, Util}

import java.awt.image.BufferedImage
import java.awt.{AlphaComposite, Graphics, Graphics2D}
import scala.language.postfixOps

/**
 * Created by nyxos on 28.12.16.
 */
case class PowerBam(_x: Float,
                    _y: Float,
                    var _width: Float,
                    var _height: Float,
                    initLife: Long)
  extends ElementOfPower {
  println(s"x ${_x}, y ${_y}, w ${_width}, h ${_height}")

  val life = System.currentTimeMillis() + initLife
  var x: Double = _x
  var y: Double = _y
  var width: Double = 0
  var height: Double = 0

  var i = 0


  var currentImage: Option[BufferedImage] = if (powerMode.isSingleBamImagePerEvent) {
    val bis = ImageUtil.imagesForPath(powerMode.bamImageFolder)
    val img = bis((math.random() * (bis.size - 1)) % bis.size toInt)
    val ih = img.getWidth / img.getHeight.toDouble
    println(s"img ${img.getWidth} / ${img.getHeight}")
    println(s"old ${_width} ${_height}")
    if (ih > 1) {
      _height = (_height / ih).toFloat
      _width = _width
    } else {
      _height = _height
      _width = (_width * ih).toFloat
    }
    println(s"new ${_width} ${_height}")
    Option(img)
  } else {
    None
  }

  override def update(delta: Float): Boolean = {
    if (alive) {
      if (!powerMode.isSingleBamImagePerEvent) {
        val bis = ImageUtil.imagesForPath(powerMode.bamImageFolder)
        if (bis.nonEmpty) {
          currentImage = Option(bis(i % bis.size))
        }
      }
      i += 1
      x = _x + (0.5 * _width) - (0.5 * _width * lifeFactor)
      y = _y + (0.5 * _height) - (0.5 * _height * lifeFactor)
      width = _width * lifeFactor
      height = _height * lifeFactor
    }
    !alive
  }

  override def render(g: Graphics, dxx: Int, dyy: Int): Unit = {
    currentImage.foreach { currentImage =>
      if (alive) {
        val g2d: Graphics2D = g.create.asInstanceOf[Graphics2D]
        g2d.setComposite(
          AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
            Util.alpha(0.9f * (1 - lifeFactor))))
        g2d.drawImage(currentImage,
          x + dxx toInt,
          y + dyy toInt,
          width toInt,
          height toInt,
          null)
        g2d.dispose()
      }
    }

  }
}
