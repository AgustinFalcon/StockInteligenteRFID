package com.stonecoders.stockinteligenterfid

import android.util.Log
import com.stonecoders.stockinteligenterfid.entities.Articulo
import com.stonecoders.stockinteligenterfid.entities.TagInfo
import com.stonecoders.stockinteligenterfid.room.repositories.ArticlesRepository
import com.stonecoders.stockinteligenterfid.room.repositories.DepositoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ArticleBuilderTest {
    val elems = listOf("008", "1", "000A04DF", "002", "337")

    @Test
    fun splittingIsCorrect(){
        val tag = "0081000A04DF002337"
        println(tag.substring(12,15))
        assert(tag.substring(4,12).equals(elems[2]))
        assert(tag.substring(12,15).equals(elems[3]))
        assert(tag.substring(startIndex = 15).equals(elems[4]))
        /*
            val parentIdentifier = _epc.epc?.substring(startIndex = 4, endIndex = 12)
                val metadatadetalle1 = _epc.epc?.substring(startIndex = 13, endIndex = 15)
                val metadatadetalle2 = _epc.epc?.substring(startIndex = 16)
             */
    }

    @Test
    fun conversionIsCorrect() = runBlocking {
        val depoRepo = DepositoRepository("https://wanama.stockinteligente.com")
        val articleRepo = ArticlesRepository("https://wanama.stockinteligente.com")
            val initialResponse = depoRepo.getInitialData().body()!!.meta_metadatadetalle
            val articleResponse = articleRepo.getAllArticles("5").filter { it.metadatadetalle1 == null }

            val tag = "0081000A04DF002337"

            val builder = Articulo.Companion.Builder()



            val article = builder.metadata(initialResponse).parents(articleResponse).epc(TagInfo(tag,null)).build()
        if (article != null) {
            println(article)
            assert(!article.nombre.equals("TAPADO NELLA"))
        }
    }

    @Test
    fun articleExists() = runBlocking {

    }

    @Test
    fun numberConversion(){
        val hexString = arrayOf("008", "000A04DF","002","337")
        val expectedDecimal = arrayOf(8, 656607,2,823)
        val index = 3
        assert(java.lang.Long.parseLong(hexString[index], 16) == expectedDecimal.get(index).toLong())



    }
}