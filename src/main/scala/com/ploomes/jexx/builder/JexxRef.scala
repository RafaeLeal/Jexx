package com.ploomes.jexx.builder

/**
  * Created by rafaeleal on 20/05/17.
  */
case class JexxRef(Ref: String) {
  def matched = s"$${$Ref}"
}
