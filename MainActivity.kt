package alarm.shevchenko.v.jsoup

import androidx.appcompat.app.AppCompatActivity

import android.os.AsyncTask
import android.os.Bundle

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

import java.io.IOException
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private val partOfSpeech = ArrayList<String>()   //частица речи (сущ, глаг, прилаг)
    private val translations = ArrayList<String>()   //переводы слов
    private val contextSource = ArrayList<String>()  //контекст источник
    private val contextTarget = ArrayList<String>()  //контекст перевод


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val myTask = MyTask()
        myTask.execute("go") //Слово


    }


    internal inner class MyTask : AsyncTask<String, Void, Void>() {

        override fun doInBackground(vararg strings: String): Void? {
            var doc: Document? = null
            var word: String
            word = strings[0]

            try {
                doc = Jsoup.connect("https://context.reverso.net/перевод/английский-русский/$word").get()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            // Получение переводов слова
            val element = doc!!.getElementsByClass("translation")
            val partElement = ArrayList<Element>()

            for (i in element.indices) {
                partElement.add(element[i])
                translations.add(partElement[i].text())
            }
            translations.removeAt(0)


            // Получение части речи (сущ, глагол...)
            val elements1 = doc.select("div.pos-mark")
            val el1 = ArrayList<Elements>()

            for (i in elements1.indices) {
                el1.add(elements1[i].select("span"))
                partOfSpeech.add(el1[i].attr("title"))
            }

            // Получение контекста (источник)
            val con = doc.getElementById("examples-content")
            val con2 = con.getElementsByClass("src ltr")
            val con3 = con2.select("span.text")

            for (i in con3.indices) {
                contextSource.add(con3[i].text())
            }

            // Получение контекста (перевод)
            val cont = doc.getElementById("examples-content")
            val cont2 = cont.getElementsByClass("trg ltr")
            val cont3 = cont2.select("span.text")

            for (i in cont3.indices) {
                contextTarget.add(cont3[i].text())
            }

            return null
        }

    }


}
