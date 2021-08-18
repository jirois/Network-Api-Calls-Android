package com.jinncyapps.apidemo

import android.app.Dialog
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ApiCallLoginAsyncTask().execute()
    }

    private inner class ApiCallLoginAsyncTask():
        AsyncTask<Any, Void, String>(){
        // A variable for custom Progress Dialog
        private lateinit var customProgressDialog: Dialog

        override fun onPreExecute() {
            super.onPreExecute()
            showProgressDialog()
        }

        override fun doInBackground(vararg params: Any?): String {
            var result: String

            var connection: HttpURLConnection? = null
            try {
                val url = URL("https://run.mocky.io/v3/28ad7ba2-9f7b-4bd7-b257-688ffa757d27")
                connection = url.openConnection() as HttpURLConnection

                /**
                 * A URL connection can be used for input and/or output.  Set the DoOutput
                 * flag to true if you intend to use the URL connection for output,
                 * false if not.  The default is false.
                 */
                connection.doOutput = true
                connection.doInput = true

                val httpResult: Int = connection.responseCode //Get a response code OK

                if (httpResult == HttpURLConnection.HTTP_OK){
                    // returns an input stream that reads from the open connection

                    val inputStream = connection.inputStream

                    // Creates a buffering character-input stream that uses default-size input buffer
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val sb = StringBuilder()
                    var line: String?

                    try {
                        /**
                         * Read a line of text
                         */
                        while (reader.readLine().also { line = it} != null){
                            sb.append(line + "\n")
                        }
                    }catch (e: IOException) {
                        e.printStackTrace()
                    } finally {
                        try {
                            // Closes this input stream and free system resources associated with it
                            inputStream.close()
                        } catch (e: IOException){
                            e.printStackTrace()
                        }
                    }
                    result = sb.toString()
                } else {
                    //Gets the HTTP response message
                    result = connection.responseMessage
                }
            } catch (e: SocketTimeoutException){
                result = "Connection Timeout"
            }catch (e: Exception){
                result = "Error: " + e.message
            } finally {
                connection?.disconnect()
            }
            // Return result to show onPostExecute
            return result
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)

            cancelProgressDialog()

            Log.i("JSON Response Result", result)

            /**
             * Create a new with name/value mappings from the JSON
             */
            val jsonObject = JSONObject(result)


            // Returns the value mapped by {name} it it exists
            val message = jsonObject.optString("message")
            Log.i("Message", message)

            // Returns the value mapped by {name} it it exists
            val userId = jsonObject.optInt("user_id")
            Log.i("Message", "$userId")

            // Returns the value mapped by {name} it it exists
            val mobileNumber = jsonObject.optLong("mobile")
            Log.i("Message", "$mobileNumber")

            // Returns the value mapped by {name} if it exists.
            val profileDetailsObject = jsonObject.optJSONObject("profile_details")

            val isProfileCompleted = profileDetailsObject.optBoolean("is_profile_completed")
            Log.i("Is Profile Completed", "$isProfileCompleted")

            val rating = profileDetailsObject.optDouble("rating")
            Log.i("Rating", "$rating")

            // Returns the value mapped by {name} if it exists.
            val dataListArray = jsonObject.optJSONArray("data_list")
            Log.i("Data List Size", "${dataListArray.length()}")

            for (item in 0 until dataListArray.length()) {
                Log.i("Value $item", "${dataListArray[item]}")

                // Returns the value mapped by {name} if it exists.
                val dataItemObject: JSONObject = dataListArray[item] as JSONObject

                val id = dataItemObject.optString("id")
                Log.i("ID", "$id")

                val value = dataItemObject.optString("value")
                Log.i("Value", "$value")
            }

        }


        private fun showProgressDialog(){
            customProgressDialog = Dialog(this@MainActivity)

            //Inflate the customdialog layout
            customProgressDialog.setContentView(R.layout.dialog_custom_progress)

            //Start the dialog and display it on the screen
            customProgressDialog.show()
        }


        private fun cancelProgressDialog(){
            customProgressDialog.dismiss()

        }

    }


}