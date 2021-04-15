package com.stonecoders.stockinteligenterfid.entities

data class ConfigurationResponse(
    val POS: List<POS>,
    val bancos: List<Banco>,
    val cuentas: List<Cuenta>,
    val depositos: List<Deposito>,
    val divisas: List<Divisa>,
    val lectores: List<Lectore>,
    val meta_metadata_x_linea: List<MetaMetadataXLinea>,
    val meta_metadatadetalle: List<MetaMetadatadetalle>,
    val parametros: Parametros,
    val posnet: List<Posnet>,
    val promociones: List<Promocione>,
    val provincias: List<Provincia>,
    val sociedad: List<Sociedad>,
    val tarjetas: List<Tarjeta>,
    val tarjetas_bancos: List<TarjetasBanco>,
    val tarjetas_formapagocuotas: List<TarjetasFormapagocuota>,
    val tarjetas_formaspagos: List<TarjetasFormaspago>,
    val tarjetas_x_posnet: List<TarjetasXPosnet>,
    val tipos_iva: List<TiposIva>,
    val usuarios: List<Usuario>,
    val vendedores: List<Vendedore>
)