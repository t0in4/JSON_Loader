package com.e.loader

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*

import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val spannable =  SpannableString("Изучайте\nактуальные темы")
        spannable.setSpan(
                ForegroundColorSpan(Color.RED),
                9, // start
                24, // end
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        text_view.text = spannable
        CallTask().execute()




    }
    private inner class CallTask(): AsyncTask<Any, Void, String>(){

        private lateinit var customProgressDialog: Dialog
        override fun onPreExecute() {
            super.onPreExecute()

            showProgressDialog()

        }

        override fun doInBackground(vararg params: Any?): String {
            var result: String

            var connection: HttpURLConnection? = null

            try{
                val url = URL("https://gitcdn.link/repo/netology-code/rn-task/master/netology.json")
                connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.doOutput = true
                val httpResult: Int = connection.responseCode

                if (httpResult == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val stringBuilder = StringBuilder()
                    var line: String?
                    try {
                        while(reader.readLine().also {line = it} != null){
                            stringBuilder.append(line+"\n")
                        }
                    } catch(e: IOException){
                        e.printStackTrace()
                    } finally {
                        try{
                            inputStream.close()
                        }catch(e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    result = stringBuilder.toString()
                }
                else {
                    result = connection.responseMessage
                }
            }catch (e: SocketTimeoutException) {
                result  = "Connection Timeout"
            } catch (e:Exception) {
                result = "Error :" + e.message
            }finally {
                connection?.disconnect()
            }

            return result




        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            cancelProgressDialog()
            Log.i("JSON RESPONSE RESULT", result.toString())
            val jsonObject = JSONObject(result)
            val data = jsonObject.optJSONArray("data")
            val need = mutableListOf<String>()
            for (i in 0 until data.length()) {
                var counter = 0
                need.clear()
                var ourObject: JSONObject = data[i] as JSONObject
                val groups = ourObject.getJSONArray("groups")
                val direction = ourObject.getJSONObject("direction")
                val directionTitle = direction.optString("title")
                println(directionTitle)
                for (z in 0 until groups.length()) {
                    //println("working")
                    val itemsObject: JSONObject = groups[z] as JSONObject
                    //println("working")
                    val items = itemsObject.getJSONArray("items")
                    //println("working")
                    for (x in 0 until items.length()) {
                        //println("working")
                        //println(items[x])
                        counter++
                        val titleObject: JSONObject = items[x] as JSONObject

                        val titleString = titleObject.optString("title")
                        //println(titleString)
                        need.add(titleString)

                    }

                }

                /*println(need.size)
                println(need)
                println(counter)*/
                when(directionTitle) {
                    "Маркетинг" -> {
                        val intent = Intent(this@MainActivity, MainActivity::class.java)
                        intent.putExtra(Constants.MARKETING, need.size.toString())
                        val username = intent.getStringExtra(Constants.MARKETING)
                        marketing.text = username
                        startActivity(intent)
                        finish()

                    }
                    "Бизнес и управление" -> {
                        val intent = Intent(this@MainActivity, MainActivity::class.java)
                        intent.putExtra(Constants.BUSINESS, need.size.toString())
                        val username = intent.getStringExtra(Constants.BUSINESS)
                        business.text = username
                        startActivity(intent)
                        finish()
                    }
                    "Программирование" -> {
                        val intent = Intent(this@MainActivity, MainActivity::class.java)
                        intent.putExtra(Constants.CODING, need.size.toString())
                        val username = intent.getStringExtra(Constants.CODING)
                        coding.text = username
                        startActivity(intent)
                        finish()
                    }
                    "Дизайн и UX" -> {
                        val intent = Intent(this@MainActivity, MainActivity::class.java)
                        intent.putExtra(Constants.DESIGN, need.size.toString())
                        val username = intent.getStringExtra(Constants.DESIGN)
                        design.text = username
                        startActivity(intent)
                        finish()
                    }
                    "Аналитика" -> {
                        val intent = Intent(this@MainActivity, MainActivity::class.java)
                        intent.putExtra(Constants.ANALYTIC, need.size.toString())
                        val username = intent.getStringExtra(Constants.ANALYTIC)
                        analytic.text = username
                        startActivity(intent)
                        finish()
                    }
                    "MBA" -> {
                        val intent = Intent(this@MainActivity, MainActivity::class.java)
                        intent.putExtra(Constants.MBA, need.size.toString())
                        val username = intent.getStringExtra(Constants.MBA)
                        mba.text = username
                        startActivity(intent)
                        finish()
                    }
                }
                }
        }




        private fun showProgressDialog() {
            customProgressDialog = Dialog(this@MainActivity)
            customProgressDialog.setContentView(R.layout.dialog_custom_progress)
            customProgressDialog.show()
        }
        private fun cancelProgressDialog() {
            customProgressDialog.dismiss()
        }


    }

}