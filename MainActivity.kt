package alarm.shevchenko.v.jsoup

import androidx.appcompat.app.AppCompatActivity

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

import java.io.IOException
import java.lang.StringBuilder
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private val translations = ArrayList<Translation>() //Массив - часть речи, массив переводов
    private val context = ArrayList<Context>()          //Массив - контекст источник, контекст перевод
    private val example = ArrayList<Example>()          //Массив - предложение, количество


    // implementation 'org.jsoup:jsoup:1.12.1'   (Библиотека JSOUP)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val reversoContext = ReversoContext()
        reversoContext.execute("home", "английский", "русский")   //Переменные: слово; язык источник; язык перевод.
    }





    internal inner class ReversoContext : AsyncTask<String, Void, Void>() {
        override fun doInBackground(vararg strings: String): Void? {
            val doc: Document?
            val word = strings[0]
            val langSource = strings[1]
            val langTarget = strings[2]


            try {
                doc = Jsoup.connect("https://context.reverso.net/перевод/$langSource-$langTarget/$word").get()

                // Получение переводов слова
                if(doc != null){

                    val element = doc.getElementsByClass("translation")
                    val partElement = ArrayList<Element>()
                    val translations1 = ArrayList<String>()

                    for (i in element.indices) {
                        partElement.add(element[i])
                        translations1.add(partElement[i].text())
                    }
                    translations1.removeAt(0)


                    // Получение части речи и перевода(сущ, глагол...)
                    val elements1 = doc.getElementsByClass("translation")
                    elements1.removeAt(0)
                    val el1 = ArrayList<Element>()
                    val mPartOfSpeech = ArrayList<String>()  //части речи(18) = переводы(18)

                    for (i in elements1.indices) {
                        el1.add(elements1[i])

                        if(el1.get(i).select("div.pos-mark").toString() != "") {
                            mPartOfSpeech.add(el1.get(i).select("div.pos-mark").select("span").attr("title"))
                        }else{
                            mPartOfSpeech.add("null")
                        }
                    }

                    var hash = HashMap<String, String>()
                    var types = ArrayList<String>()

                    for(i in mPartOfSpeech.indices){
                        hash.put(mPartOfSpeech.get(i), "")
                    }
                    for(i in hash.keys.indices) {
                        types.add(hash.keys.elementAt(i))
                    }

                    for(i in types.indices){  // от 0 до 5

                        var tr1 = Translation()

                        for(o in mPartOfSpeech.indices){
                            if(mPartOfSpeech[o].contains(types.get(i))){
                                tr1.partOfSpeech = types[i]
                                tr1.translation.add(translations1[o])
                            }
                        }
                        translations.add(tr1)
                    }


                    // Получение контекста (источник)
                    val con = doc.getElementById("examples-content")
                    val con2 = con.getElementsByClass("src ltr")
                    val con3 = con2.select("span.text")
                    val contextSource = ArrayList<String>()
                    val contextTarget = ArrayList<String>()

                    for (i in con3.indices) {
                        contextSource.add(con3[i].text())
                    }


                    // Получение контекста (перевод)
                    val cont = doc.getElementById("examples-content")
                    val cont2 = cont.getElementsByClass("trg ltr")
                    val cont3 = cont2.select("span.text")

                    for (i in cont3.indices) {
                        contextTarget.add(cont3[i].text())

                        val con = Context()
                        con.contextSource = contextSource.get(i)
                        con.contextTarget = contextTarget.get(i)
                        context.add(con)
                    }


                    //Получение предложений
                    val sugg: ArrayList<Element> = doc.getElementsByClass("suggestion")
                    val count: ArrayList<Element> = doc.select("span.figure")

                    sugg.removeAt(0)

                    for(i in 0 until (sugg.size/2)){
                        val ex = Example()

                        ex.suggestion = sugg.get(i).getElementsByClass("text").text()
                        ex.count = count.get(i).text()

                        example.add(ex)
                    }


                    Log.d("tag1", "Существительные--  " + translations[0].translation.toString())
                    

                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }
    }

    class Translation{
        var partOfSpeech: String = ""
        var translation = ArrayList<String>()
    }

    class Context{
        var contextSource: String = ""
        var contextTarget: String = ""
    }

    class Example{
        var suggestion = ""
        var count = ""
    }



}
