package _power.mode

/**
 * Created by nyxos on 04.01.17.
 */
trait Power {
  def powerMode: PowerMode = {
    PowerMode.getInstance
  }

  def logger = PowerMode.logger
}
