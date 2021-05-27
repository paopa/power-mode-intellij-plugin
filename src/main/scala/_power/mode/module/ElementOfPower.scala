package _power.mode.module

import _power.mode.Power

import java.awt.Graphics

/**
 * Created by nyxos on 10.03.16.
 */
trait ElementOfPower extends Power {

  def update(delta: Float): Boolean

  def render(g: Graphics, dxx: Int, dyy: Int)

  def life: Long

  def initLife: Long

  def lifeFactor: Float = {
    1 - ((life - System.currentTimeMillis()) / initLife.toFloat)
  }

  def alive: Boolean = {
    life > System
      .currentTimeMillis() && powerMode.isEnabled //&& width >= 0 && width <= _width
  }
}
