package com.ploomes.jexx.notation

import com.ploomes.jexx.types.NavigationChunk

/**
  * Created by rafaeleal on 20/05/17.
  */
trait NavigationNotation {
  def getValue(data: Any, navList: List[NavigationChunk]) : Any
}
