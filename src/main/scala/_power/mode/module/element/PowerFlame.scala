package _power.mode.module.element

import _power.mode.module.ElementOfPower
import _power.mode.{ImageUtil, Util}

import java.awt.image.BufferedImage
import java.awt.{AlphaComposite, Graphics, Graphics2D}

case class PowerFlame(_x: Int,
                      _y: Int,
                      _width: Int,
                      _height: Int,
                      initLife: Long,
                      up: Boolean)
    extends ElementOfPower {
  val life = System.currentTimeMillis() + initLife
  var x = _x
  var y = _y
  var width = 0
  var height = 0

  var i = 0
  var currentImage: BufferedImage = null

  override def update(delta: Float): Boolean = {
    if (alive) {
      val flameImages1 = ImageUtil.imagesForPath(powerMode.flameImageFolder)
      if (flameImages1.nonEmpty) {
        currentImage = flameImages1(i % flameImages1.size)
      }
      i += 1
      x = _x - (0.5 * _width * lifeFactor).toInt
      if (up)
        y = _y - (1.1 * _height * lifeFactor).toInt
      else
        y = _y + (0.25 * _height * lifeFactor).toInt
      width = (_width * lifeFactor).toInt
      height = (_height * lifeFactor).toInt
    }
    !alive
  }

  override def render(g: Graphics, dxx: Int, dyy: Int): Unit = {
    if (alive) {

      val g2d: Graphics2D = g.create.asInstanceOf[Graphics2D]
      g2d.setComposite(
        AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                   Util.alpha(0.9f * (1 - lifeFactor))))

      if (up) {
        if (currentImage != null)
          g2d.drawImage(currentImage, x + dxx, y + dyy, width, height, null)
      } else {
        // flip horizontally
        if (currentImage != null)
          g2d.drawImage(currentImage,
                        x + dxx,
                        y + dyy + height,
                        width,
                        -height,
                        null)
      }
      g2d.dispose()
    }
  }

}
