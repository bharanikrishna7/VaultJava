package net.chekuri.vault.models

import org.apache.logging.log4j.scala.Logging

object VaultModels extends Logging {

  object Request {

    case class AppRoleLoginModel(
      role_id: String,
      secret_id: String
    ) extends JsonTraits

  }

  object Response {

    trait ResponseTemplate extends JsonTraits {
      def auth: Option[AuthModel]

      def warnings: Option[List[String]]

      def wrap_info: Option[String]

      def lease_duration: Long

      def renewable: Boolean

      def lease_id: Option[String]
    }

    case class AuthModel(
      renewable: Boolean,
      lease_duration: Long,
      token_policies: List[String],
      accessor: String,
      client_token: String,
      metadata: Option[String]
    ) extends JsonTraits

    case class AppRoleDataModel(
      token_ttl: Long,
      token_max_ttl: Long,
      secret_id_ttl: Long,
      secret_id_num_uses: Long,
      token_policies: List[String],
      period: Long,
      bind_secret_id: Boolean,
      bound_cidr_list: List[String]
    ) extends JsonTraits

    case class AppRoleAuthModel(
      override val auth: Option[AuthModel],
      override val warnings: Option[List[String]],
      override val wrap_info: Option[String],
      override val lease_duration: Long,
      override val renewable: Boolean,
      override val lease_id: Option[String]
    ) extends ResponseTemplate

    case class AppRolePropertiesModel(
      data: AppRoleDataModel,
      override val auth: Option[AuthModel],
      override val warnings: Option[List[String]],
      override val wrap_info: Option[String],
      override val lease_duration: Long,
      override val renewable: Boolean,
      override val lease_id: Option[String]
    ) extends ResponseTemplate

    case class ReadSecretModel(
      data: Map[String, String],
      override val auth: Option[AuthModel],
      override val warnings: Option[List[String]],
      override val wrap_info: Option[String],
      override val lease_duration: Long,
      override val renewable: Boolean,
      override val lease_id: Option[String]
    ) extends ResponseTemplate

  }

  object Http {

    case class VaultHttpResponse[T](
      code: Int,
      message: String,
      headers: Map[String, String],
      body: T
    ) extends JsonTraits

    case class VaultHttpBodyModel(body: String, content_type: String) extends JsonTraits

  }

}
