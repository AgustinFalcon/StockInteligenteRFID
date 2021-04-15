package com.stonecoders.stockinteligenterfid.entities

import android.util.Log
import java.lang.NumberFormatException


data class Articulo(
        var articulo: String,
        var cbarra: String,
        var codigo: String,
        var codigoint: String,
        var epc: String,
        var estado: String,
        var imagen: Any,
        var iva: String,
        var linea: String,
        var metadatadetalle1: String?,
        var metadatadetalle2: String?,
        var nombre: String,
        var precio: String
) {
    companion object {

        class Builder {
            private val TAG = "BuilderInfo"
            private var _dictionary: List<MetaMetadatadetalle> = emptyList()
            private var _epc: TagInfo = TagInfo(null, null)
            private var _parents = emptyList<Articulo>()
            //TODO: Company validation

            fun metadata(d: List<MetaMetadatadetalle>) = apply {
                _dictionary = d
            }

            fun parents(p: List<Articulo>) = apply {
                _parents = p
            }

            fun epc(e: TagInfo) = apply {
                _epc = e
            }


            fun build(): Articulo? {
                require(_dictionary.isNotEmpty())
                require(_parents.isNotEmpty())
                require(_epc.epc != null)

                val companyLine = _epc.epc?.substring(startIndex = 0, endIndex = 3)
                val parentIdentifier = _epc.epc?.substring(startIndex =4 , endIndex = 12)
                val metadatadetalle1 = _epc.epc?.substring(startIndex = 12, endIndex = 15)
                val metadatadetalle2 = _epc.epc?.substring(startIndex = 15)

                try {
                    val parsedParent = java.lang.Long.parseLong(parentIdentifier!!, 16)
                    Log.d(TAG, "PArsed parent $parsedParent")
                    val parent = _parents.find { p -> p.epc.toLong() == parsedParent}

                    val parsedMeta1 = java.lang.Long.parseLong(metadatadetalle1!!, 16)
                    Log.d(TAG, "PArsed meta2 $parsedMeta1")

                    val meta1 = _dictionary.find { p ->

                        p.epc.toLong() == parsedMeta1
                    }
                    val parsedMeta2 = java.lang.Long.parseLong(metadatadetalle2!!, 16)


                    val meta2 = _dictionary.find { p ->
                        p.epc.toLong() == parsedMeta2
                    }
                    parent.let {
                        it?.metadatadetalle1 = meta1?.descripcion.toString()
                        it?.metadatadetalle2 = meta2?.descripcion.toString()
                        return@build it
                    }
                } catch (nfe: NumberFormatException){
                    Log.w(TAG, "Error is ${nfe.stackTrace}" )
                }
                return null
            }


            /**
             * public static bool EpcToArticulo(string sEPC, out Articulo articulo, out string error)
            {
            // int empresa = int.Parse(sEPC.Substring(0, 3), System.Globalization.NumberStyles.HexNumber);
            string empresalinea = sEPC.Substring(0, 3);
            string articulopadre = sEPC.Substring(4, 8);
            string metadatadetalle1 = sEPC.Substring(12, 3);
            string metadatadetalle2 = sEPC.Substring(15, 3);
            error = null;
            articulo = null;

            //EMPRESA SE USA PARA MULTIMARCA
            if (BDatos.Datos.Hijos.TryGetValue(empresalinea + "1" + articulopadre + metadatadetalle1 + metadatadetalle2, out articulo)) return true;
             */


        }
    }



}