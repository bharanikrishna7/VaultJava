package net.chekuri.vault.models

import org.json4s.DefaultFormats
import org.json4s.native.Serialization

/**
  * Trait containing methods to convert a class to Json.
  */
trait JsonTraits {
  implicit val formats: DefaultFormats.type = DefaultFormats

  def toJson: String = Serialization.writePretty(this)

  override def toString: String = this.toJson
}
