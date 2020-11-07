package com.appzoro.milton.utility

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.app.Service
import android.content.ContentResolver
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.media.ExifInterface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.text.SpannableString
import android.text.TextUtils
import android.text.format.DateUtils
import android.text.style.UnderlineSpan
import android.util.Base64
import android.util.Patterns
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import com.appzoro.milton.R
import com.appzoro.milton.network.ApiEndPoint
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.alert_dialog_with_image.*
import kotlinx.android.synthetic.main.alert_dialog_with_two_button.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import kotlin.math.pow
import kotlin.math.roundToInt

class Utils {
    companion object {

        fun loadImage(
            mActivity: Context,
            path: String,
            ivImageView: ImageView,
            mProgress: ProgressBar
        ) {
            AppLogger.e("Image url $path")
            if (path.isNotEmpty()) {
                mProgress.visibility = View.VISIBLE
                val url = ApiEndPoint.BASE_MEDIA_URL + path

                val ro = RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                Glide.with(mActivity).load(url).error(R.drawable.ic_camera)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            mProgress.visibility = View.GONE
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            mProgress.visibility = View.GONE
                            return false
                        }

                    })
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .apply(ro)
                    .into(ivImageView)
            }

        }

        fun loadImage(mActivity: Context, path: String, ivImageView: ImageView) {
            AppLogger.e("Image url $path")
            if (path.isNotEmpty()) {
                val url = ApiEndPoint.BASE_MEDIA_URL + path
                Glide.with(mActivity).load(url).error(R.drawable.ic_camera)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivImageView)
            }

        }


        //set custom title on support toolbar
        fun setDefaultTitleCenter(
            actionBar: ActionBar?,
            pageTitle: String,
            homeActivity: Activity,
            flagToShow: Boolean
        ) {
            val d = ContextCompat.getDrawable(homeActivity, R.drawable.ic_gradient_top_header)
            val viewActionBar =
                homeActivity.layoutInflater.inflate(R.layout.actionbar_title_text_layout, null)
            val params = ActionBar.LayoutParams(//Center the text view in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER
            )
            actionBar?.setBackgroundDrawable(d)
            actionBar?.elevation = 0f
            val textViewTitle = viewActionBar.findViewById(R.id.actionbar_textView) as TextView
            textViewTitle.text = pageTitle
            actionBar?.setCustomView(viewActionBar, params)
            actionBar?.setDisplayShowCustomEnabled(true)
            actionBar?.setDisplayShowTitleEnabled(false)
            actionBar?.setDisplayHomeAsUpEnabled(flagToShow)
            actionBar?.setHomeAsUpIndicator(R.drawable.ic_white_back)
            actionBar?.setIcon(android.R.color.transparent)
            actionBar?.setHomeButtonEnabled(false)
        }

        //get dynamic device width
        fun getDeviceWidth(): Int {
            return Resources.getSystem().displayMetrics.widthPixels
        }

        //get dynamic device height
        fun getDeviceHeight(): Int {
            return Resources.getSystem().displayMetrics.heightPixels
        }

        //find greeting of the day
        fun greetingsOfTheDay(): String {
            val c = Calendar.getInstance(Locale.US)
            val timeOfDay = c.get(Calendar.HOUR_OF_DAY)
            var dayGreeting = ""
            when (timeOfDay) {
                in 0..11 -> dayGreeting =
                    "Good Morning..!!"
                in 12..15 -> dayGreeting =
                    "Good Afternoon..!!"
                in 16..20 -> dayGreeting =
                    "Good Evening..!!"
                in 21..23 -> dayGreeting =
                    "Good Night..!!"
            }
            return dayGreeting
        }

        //show short toast message
        fun showToast(context: Context, msg: String) {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }


        /**
         * Hides soft keyboard.
         *
         * @param editText EditText which has focus
         */
        fun hideSoftKeyboard(
            editText: EditText?,
            mActivity: Activity
        ) {
            if (editText == null)
                return
            val imm = mActivity.getSystemService(Service.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(editText.windowToken, 0)
        }


        /*
        * show dialog
        * */
        @JvmStatic
        fun showLoadingDialog(context: Context): ProgressDialog {
            val progressDialog = ProgressDialog(context, R.style.ProgressBar)

            progressDialog.show()
            if (progressDialog.window != null) {
                progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }

            progressDialog.setContentView(R.layout.progress_dialog)
            progressDialog.isIndeterminate = true
            progressDialog.setCancelable(false)
            progressDialog.setCanceledOnTouchOutside(false)

            return progressDialog
        }

        @Throws(Exception::class)
        fun compressImage(file: File, mActivity: Activity): File {

            return Compressor(mActivity)
                .setMaxWidth(400)
                .setMaxHeight(400)
                .setQuality(60)
                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                .setDestinationDirectoryPath(
                    Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES
                    ).absolutePath
                )
                .compressToFile(file)
        }

        /*
        * check mobile number pattern
        * @mobileNumber
        * */
        fun mobileNumberPatternValidation(mobileNumber: String): Boolean {
            val p = Pattern.compile("^[1-9][0-9]*\$")
            val m = p.matcher(mobileNumber)
            return !(m.matches() && (mobileNumber.length in 9..10))

        }

        /*
        * rotate bitmap image
        * */
        fun rotateBitmap(source: Bitmap, angle: Int): Bitmap {

            val matrix = Matrix()
            matrix.postRotate(angle.toFloat())
            return Bitmap.createBitmap(
                source, 0, 0, source.width, source.height,
                matrix, true
            )
        }

        /*
        for getting bitmap
        */
        fun getBitmap(uri: Uri, mContext: Context): Bitmap? {
            val returnedBitmap: Bitmap
            val mContentResolver: ContentResolver
            try {
                mContentResolver = mContext.contentResolver
                var ins = mContentResolver.openInputStream(uri)
                //Decode image size
                val o = BitmapFactory.Options()
                o.inJustDecodeBounds = true
                BitmapFactory.decodeStream(ins, null, o)
                Objects.requireNonNull<InputStream>(ins).close()
                var scale = 1
                val IMAGE_MAX_SIZE = 1024
                if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
                    scale = 2.0.pow(
                        (Math.log(
                            IMAGE_MAX_SIZE / Math.max(
                                o.outHeight,
                                o.outWidth
                            ).toDouble()
                        ) / Math.log(0.5)).roundToInt().toInt().toDouble()
                    ).toInt()
                }

                val o2 = BitmapFactory.Options()
                o2.inSampleSize = scale
                ins = mContentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(ins, null, o2)
                val bos = ByteArrayOutputStream()
                Objects.requireNonNull<Bitmap>(bitmap)
                    .compress(Bitmap.CompressFormat.JPEG, 100, bos)
                Objects.requireNonNull<InputStream>(ins).close()

                //First check
                val ei = ExifInterface(Objects.requireNonNull<String>(uri.path))
                val orientation = ei.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )

                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> {
                        returnedBitmap = rotateImage(bitmap!!, 90f)
                        //Free up the memory
                        bitmap.recycle()
                    }
                    ExifInterface.ORIENTATION_ROTATE_180 -> {
                        returnedBitmap = rotateImage(bitmap!!, 180f)
                        //Free up the memory
                        bitmap.recycle()
                    }
                    ExifInterface.ORIENTATION_ROTATE_270 -> {
                        returnedBitmap = rotateImage(bitmap!!, 270f)
                        //Free up the memory
                        bitmap.recycle()
                    }
                    ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> return flip(bitmap!!, true, false)

                    ExifInterface.ORIENTATION_FLIP_VERTICAL -> return flip(bitmap!!, false, true)
                    else -> returnedBitmap = bitmap!!
                }
                return returnedBitmap
            } catch (e: IOException) {
                //L.e(e);
            }

            return null
        }

        /*
        * return image URI
        * take parameter
        * @image path
        * */
        fun getImageUri(path: String): Uri? {
            return try {
                Uri.fromFile(File(path))
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        /*
        for image scaling and rotating
         */
        private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(angle)
            return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
        }

        /*
        * flip from horizontal to vertical
        * */
        private fun flip(bitmap: Bitmap, horizontal: Boolean, vertical: Boolean): Bitmap {
            val matrix = Matrix()
            matrix.preScale(
                (if (horizontal) -1 else 1).toFloat(),
                (if (vertical) -1 else 1).toFloat()
            )
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }

        /**
         * Parse verification code
         *
         * @param message sms message
         * @return only four numbers from massage string
         */
        fun parseCode(message: String): String {
            val p = Pattern.compile("\\b\\d{6}\\b")
            val m = p.matcher(message)
            var code: String? = ""
            while (m.find()) {
                code = m.group(0)
            }
            return code!!
        }


        // Place pattern "-" to CIN and NIF
        fun maskTextPattern(value: String, positions: IntArray, type: String): String {
            val stringBuffer = StringBuffer(value)
            try {
                for (i in positions.indices) {
                    stringBuffer.insert(positions[i], type)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return stringBuffer.toString()
        }


        /*
        * decode image from Base64 String
        * */
        fun returnBitmapImageFromBase64(profilePicture: String): Bitmap {
            val imageBytes = Base64.decode(profilePicture, Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            return decodedImage
        }

        /*
        * show keyboard
        * */
        fun showKeyboard(context: Context, view: View) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }

        /*
        * hide keyboard
        * */
        fun View.hideKeyboard() {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(windowToken, 0)
        }


        fun getConnectionType(context: Context): Int {
            var result = 0 // Returns connection type. 0: none; 1: mobile data; 2: wifi
            val cm =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cm?.run {
                    cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                        if (hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                            result = 2
                        } else if (hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                            result = 1
                        }
                    }
                }
            } else {
                cm?.run {
                    cm.activeNetworkInfo?.run {
                        if (type == ConnectivityManager.TYPE_WIFI) {
                            result = 2
                        } else if (type == ConnectivityManager.TYPE_MOBILE) {
                            result = 1
                        }
                    }
                }
            }
            return result
        }

        /*
        * show error alert dialog
        * */
        fun showErrorAlertDialog(
            mActivity: Activity,
            mImage: Int,
            mTitle: String,
            mDescription: String,
            mButtonName: String
        ) {
            AlertDialogView.showDialog(
                mActivity,
                mImage, mTitle,
                mDescription, mButtonName
            )
        }

        /*
        * open drawer if close
        * close if drawer is open
        * @param drawerLayout
        * */
        fun drawerOpenClose(drawerLayout: DrawerLayout) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.closeDrawer(
                GravityCompat.START
            )
            else drawerLayout.openDrawer(
                GravityCompat.START
            )
        }

        /*
        * show snack bar for msg
        * */
        fun showSnackBarMsg(view: View, msg: String, actionName: String) {
            Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
                .setAction(actionName, null).show()
        }


        // yyyy/mm/dd
        fun dateConversion(jobCompleteDate: String): String {
            val year = jobCompleteDate.substring(0, 4)
            var month = jobCompleteDate.substring(5, 7)
            val date = jobCompleteDate.substring(8, 10)
            when (month) {
                "01" -> {
                    month = "Jan"
                }
                ("02") -> {
                    month = "Feb"
                }
                ("03") -> {
                    month = "Mar"
                }
                ("04") -> {
                    month = "Apr"
                }
                ("05") -> {
                    month = "May"
                }
                ("06") -> {
                    month = "Jun"
                }
                ("07") -> {
                    month = "Jul"
                }
                ("08") -> {
                    month = "Aug"
                }
                ("09") -> {
                    month = "Sep"
                }
                ("10") -> {
                    month = "Oct"
                }
                ("11") -> {
                    month = "Nov"
                }
                ("12") -> {
                    month = "Dec"
                }
            }
            return "$date $month $year"
        }

        // dd/mm/yyyy
        fun dateConversionDDMMYYYY(jobCompleteDate: String): String {
            val date = jobCompleteDate.substring(0, 2)
            var month = jobCompleteDate.substring(3, 5)
            val year = jobCompleteDate.substring(6, 10)
            when (month) {
                ("01") -> {
                    month = "January"
                }
                ("02") -> {
                    month = "February"
                }
                ("03") -> {
                    month = "March"
                }
                ("04") -> {
                    month = "April"
                }
                ("05") -> {
                    month = "May"
                }
                ("06") -> {
                    month = "June"
                }
                ("07") -> {
                    month = "July"
                }
                ("08") -> {
                    month = "August"
                }
                ("09") -> {
                    month = "September"
                }
                ("10") -> {
                    month = "October"
                }
                ("11") -> {
                    month = "November"
                }
                ("12") -> {
                    month = "December"
                }
            }
            return "$month $date "
        }

        /*
        * get current date
        * */
        fun getCurrentDate(): String {
            val calendar = Calendar.getInstance(Locale.US)
            val date = calendar.time
//            val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.US)
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            return sdf.format(date)
        }

        fun getDateInString(date: Date): String {
//            val calendar = Calendar.getInstance(Locale.US)
//            val date = calendar.time
//            val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.US)
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            return sdf.format(date)
        }

        fun getCompareYearMonth(date: Date): Boolean {
            val sdf = SimpleDateFormat("yyyy-MM", Locale.US)
            //return sdf.format(date)

            return true
        }

        fun getCurrentWeekName(): String {
            val calendar = Calendar.getInstance(Locale.US)
            val date = calendar.time
            val sdf = SimpleDateFormat("EEEE", Locale.US)
            return sdf.format(date)
        }

        fun getWeekName(date: Date): String {
            val sdf = SimpleDateFormat("EEEE", Locale.US)
            return sdf.format(date)
        }

        /*
        * get current time
        * */
        fun getCurrentTime(): String {
            val timeFormatter = SimpleDateFormat("h:mm a", Locale.US)
            val currentTimeStr = timeFormatter.format(System.currentTimeMillis())
            return currentTimeStr
        }

        /*
        * email validation
        * */
        fun isEmailValidation(email: String): Boolean {
            return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches())
        }

        /*
        * make activity in full screen
        * */
        fun makeActivityFullScreen(mActivity: Activity) {
            mActivity.requestWindowFeature(Window.FEATURE_NO_TITLE)
            mActivity.window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        /*check internet connection is on*/
        fun isOnline(context: Context): Boolean {
            val cm = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            //should check null because in airplane mode it will be null
            return netInfo != null && netInfo.isConnected
        }


        fun makeUnderLineLabel(labelName: String): String {
            val content = SpannableString(labelName)
            content.setSpan(UnderlineSpan(), 0, labelName.length, 0)
            return content.toString()
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun setImage(activity: Activity, imgView: ImageView, imageUrl: String) {
            Glide.with(activity).load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgView)


        }

        fun capitalize(str: String?): String? {
            return if (str == null || str.isEmpty()) {
                str
            } else str.substring(0, 1).toUpperCase(Locale.ROOT) + str.substring(1)
        }

        /* fun alertDialog(context: Context, message: String) {
             val alertDialog = AlertDialog.Builder(context)
                 .setTitle(context.getString(R.string.error))
                 .setMessage(message)
                 .setNeutralButton(R.string.dialog_action_ok, null)
             //alertDialog.create().show()

             val mDialog =  alertDialog.create().show()
 //            setButtonColor(context, mDialog)

         }*/

        /* fun alertDialog(context: Context, title: String, message: String) {
             val alertDialog = AlertDialog.Builder(context)
                 .setTitle(title)
                 .setMessage(message)
                 .setNeutralButton(R.string.dialog_action_ok, null)

             val mDialog =  alertDialog.create().show()

 //            setButtonColor(context, mDialog)

 //            mDialog.getButton(Dialog.BUTTON_NEGATIVE)//.setTextColor(Color.parseColor("#ffffff"))
 //                .setBackgroundColor(Color.parseColor("#ffffff"))
 //
 //            mDialog.getButton(Dialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#000000"))
 //
 //            mDialog.show()

         }*/

        fun alertDialog(context: Context, message: String) {
            val dialog = AlertDialogView.createDialog(context, R.layout.alert_dialog_with_image)
            dialog.tvMessage.text = message
            dialog.btnOk.setOnClickListener {
                dialog.dismiss()
            }

        }

        fun alertDialogWithTwoButton(
            context: Context,
            message: String
        ): Dialog {
            val dialog =
//                AlertDialogView.createDialog(context, R.layout.alert_dialog_with_two_button_student)
                AlertDialogView.createDialog(context, R.layout.alert_dialog_with_two_button)
            dialog.textViewMessage.text = message
            dialog.buttonNo.setOnClickListener {
                dialog.dismiss()
            }
            return dialog
        }

        fun forgotSuccessAlertDialog(context: Context, message: String): Dialog {
            val dialog = AlertDialogView.createDialog(context, R.layout.alert_dialog_with_image)
            dialog.tvMessage.text = message
            return dialog

        }

        /*  convert server time(2020-07-15T10:03:50.000Z) into local date( 15 july 10:03 AM)*/
        fun convertServerTimeToLocalTime(serverDate: String): String {
//            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            return try {
                val date =
                    dateFormat.parse(serverDate)
                val formatter =
                    SimpleDateFormat(
                        "dd MMM hh:mm aa",
                        Locale.US
                    )
                formatter.format(date!!)
            } catch (e: Exception) {
                ""
            }
        }

        /*  convert server time(2020-07-15T10:03:50.000Z) into day name like : Wednesday*/
        fun getDayNameFromServerTime(serverDate: String): String {
//            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            return try {
                val date =
                    dateFormat.parse(serverDate)
                val formatter =
                    SimpleDateFormat(
                        "EEEE",
                        Locale.US
                    )
                formatter.format(date!!)
            } catch (e: Exception) {
                ""
            }
        }

        /*  convert server time(2020-07-15T10:03:50.000Z) into day name like : Wednesday*/
        fun getServerTime(dateInString: String): String {
            val dateFormat1 = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val dateFormat2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            try {
                val date =
                    dateFormat1.parse(dateInString) ?: return ""
                return dateFormat2.format(date)
            } catch (e: Exception) {
                return ""
            }
        }

        /*
        * recycler data load with animation
        * */
        fun runAnimationAgain(mActivity: Activity, recyclerView: RecyclerView) {
            val controller =
                AnimationUtils.loadLayoutAnimation(mActivity, R.anim.layout_animation_up_to_down)
            recyclerView.layoutAnimation = controller
            recyclerView.adapter?.notifyDataSetChanged()
            recyclerView.scheduleLayoutAnimation()

        }

        fun getMonthYearFromDate(date: Date): String {

            //july" + "/2020
            val dateFormat = SimpleDateFormat("MMMM/yyyy/", Locale.US)
            return try {
                val str = dateFormat.format(date)
                str.substring(0, str.length).toLowerCase(Locale.US)
            } catch (e: Exception) {
                ""
            }
        }

        fun getDateFromTripDate(tripDate: String?): String {//20200828

            if (tripDate == null) {
                return ""
            }
            val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
            return try {
                val date =
                    dateFormat.parse(tripDate)
                val formatter =
                    SimpleDateFormat(
                        "MM/dd/yyyy",
                        Locale.US
                    )
                formatter.format(date ?: "")
            } catch (e: Exception) {
                ""
            }
        }

        fun getYearFromServerTime(serverDate: String): String {
//            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            return try {
                val date =
                    dateFormat.parse(serverDate)
                val formatter =
                    SimpleDateFormat(
                        "yyyy",
                        Locale.US
                    )
                formatter.format(date!!)
            } catch (e: Exception) {
                ""
            }
        }

        fun getYearFromDate(date: Date?): String {
            val formatter =
                SimpleDateFormat(
                    "yyyy",
                    Locale.US
                )
            return if (date != null) {
                formatter.format(date)
            } else {
                ""
            }
        }

        fun getDateFromServerTime(serverDate: String): String {
//            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            return try {
                val date = dateFormat.parse(serverDate)
                val formatter =
                    SimpleDateFormat(
                        "MM/dd/yyyy",
                        Locale.US
                    )
                if (date != null) {
                    formatter.format(date)
                } else {
                    ""
                }
            } catch (e: Exception) {
                ""
            }
        }

        fun compareMonth(date1: Date, date2: Date): Boolean {

            val dateFormat = SimpleDateFormat("yyyy-MM", Locale.US)
            val month1 = dateFormat.format(date1)
            val month2 = dateFormat.format(date2)
            AppLogger.e("month1 $date1 month2 $date2")
            return (month1 == month2)

        }

        fun getDifferenceInMinutes(serverDate: String): Long {
            val calendar = Calendar.getInstance(Locale.US)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)

            return try {
                val date2 = dateFormat.parse(serverDate)
                val date1 = calendar.time

                val diffInMillisec: Long = date2?.time ?: 0 - date1.time

                TimeUnit.MILLISECONDS.toMinutes(diffInMillisec)
            } catch (e: Exception) {
                0
            }
        }

        fun getCalendarFromServerTime(serverDate: String): Date? {
//            val calendar = Calendar.getInstance(Locale.US)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            return try {
                dateFormat.parse(serverDate)
            } catch (e: Exception) {
                null
            }
        }

        fun convertDateFromString(serverDate: String): Date? {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            return try {
                dateFormat.parse(serverDate)
            } catch (e: Exception) {
                null
            }
        }

        fun getWeekendNameFromDate(date: Date): Boolean {
            val sdf = SimpleDateFormat("EEEE", Locale.US)

            return try {
                val str = sdf.format(date)
                (str == "Sunday" || str == "Saturday")
            } catch (e: Exception) {
                true
            }

        }

        fun showSelectedView(
            textViewMon: MaterialTextView?,
            textViewTue: MaterialTextView?,
            textViewWed: MaterialTextView?,
            textViewThu: MaterialTextView?,
            textViewFri: MaterialTextView?,
            selectedPos: Int,
            mActivity: Activity
        ) {
            when (selectedPos) {
                1 -> {
                    textViewMon?.setBackgroundColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.colorPrimaryDark
                        )
                    )
                    textViewMon?.setTextColor(ContextCompat.getColor(mActivity, R.color.colorWhite))
                    //
                    textViewTue?.setBackgroundColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.colorWhite
                        )
                    )
                    textViewTue?.setTextColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.colorPrimaryDark
                        )
                    )

                    textViewWed?.setBackgroundColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.colorWhite
                        )
                    )
                    textViewWed?.setTextColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.colorPrimaryDark
                        )
                    )

                    textViewThu?.setBackgroundColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.colorWhite
                        )
                    )
                    textViewThu?.setTextColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.colorPrimaryDark
                        )
                    )

                    textViewFri?.setBackgroundColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.colorWhite
                        )
                    )
                    textViewFri?.setTextColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.colorPrimaryDark
                        )
                    )

                }
                2 -> {
                    textViewMon?.setBackgroundColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.colorWhite
                        )
                    )
                    textViewMon?.setTextColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.colorPrimaryDark
                        )
                    )
                    //
                    textViewTue?.setBackgroundColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.colorPrimaryDark
                        )
                    )
                    textViewTue?.setTextColor(ContextCompat.getColor(mActivity, R.color.colorWhite))

                    textViewWed?.setBackgroundColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.colorWhite
                        )
                    )
                    textViewWed?.setTextColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.colorPrimaryDark
                        )
                    )

                    textViewThu?.setBackgroundColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.colorWhite
                        )
                    )
                    textViewThu?.setTextColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.colorPrimaryDark
                        )
                    )

                    textViewFri?.setBackgroundColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.colorWhite
                        )
                    )
                    textViewFri?.setTextColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.colorPrimaryDark
                        )
                    )

                }
                3 -> {
                    textViewMon?.setBackgroundColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.colorWhite
                        )
                    )
                    textViewMon?.setTextColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.colorPrimaryDark
                        )
                    )
                    //
                    textViewTue?.setBackgroundColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.colorWhite
                        )
                    )
                    textViewTue?.setTextColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.colorPrimaryDark
                        )
                    )

                    textViewWed?.setBackgroundColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.colorPrimaryDark
                        )
                    )
                    textViewWed?.setTextColor(ContextCompat.getColor(mActivity, R.color.colorWhite))

                    textViewThu?.setBackgroundColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.colorWhite
                        )
                    )
                    textViewThu?.setTextColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.colorPrimaryDark
                        )
                    )

                    textViewFri?.setBackgroundColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.colorWhite
                        )
                    )
                    textViewFri?.setTextColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.colorPrimaryDark
                        )
                    )

                }
                4 -> {
                    textViewMon?.setBackgroundColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.colorWhite
                        )
                    )
                    textViewMon?.setTextColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.colorPrimaryDark
                        )
                    )
                    //
                    textViewTue?.setBackgroundColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.colorWhite
                        )
                    )
                    textViewTue?.setTextColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.colorPrimaryDark
                        )
                    )

                    textViewWed?.setBackgroundColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.colorWhite
                        )
                    )
                    textViewWed?.setTextColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.colorPrimaryDark
                        )
                    )

                    textViewThu?.setBackgroundColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.colorPrimaryDark
                        )
                    )
                    textViewThu?.setTextColor(ContextCompat.getColor(mActivity, R.color.colorWhite))

                    textViewFri?.setBackgroundColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.colorWhite
                        )
                    )
                    textViewFri?.setTextColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.colorPrimaryDark
                        )
                    )

                }
                5 -> {
                    textViewMon?.setBackgroundColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.colorWhite
                        )
                    )
                    textViewMon?.setTextColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.colorPrimaryDark
                        )
                    )
                    //
                    textViewTue?.setBackgroundColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.colorWhite
                        )
                    )
                    textViewTue?.setTextColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.colorPrimaryDark
                        )
                    )

                    textViewWed?.setBackgroundColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.colorWhite
                        )
                    )
                    textViewWed?.setTextColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.colorPrimaryDark
                        )
                    )

                    textViewThu?.setBackgroundColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.colorWhite
                        )
                    )
                    textViewThu?.setTextColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.colorPrimaryDark
                        )
                    )

                    textViewFri?.setBackgroundColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.colorPrimaryDark
                        )
                    )
                    textViewFri?.setTextColor(ContextCompat.getColor(mActivity, R.color.colorWhite))

                }

            }
        }

        fun getChattingMessageTime(mMessageDate: String?): String? {
            //AppLogger.e("mMessageDate  $mMessageDate")

            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            val calendar = Calendar.getInstance(Locale.US)
            val calendarCurrent = Calendar.getInstance(Locale.US)

            try {
                val date = sdf.parse(mMessageDate)
                calendar.time = date

                if (calendarCurrent.get(Calendar.DATE) == calendar.get(Calendar.DATE)
                    &&
                    ((calendarCurrent.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)))
                    &&
                    ((calendarCurrent.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)))
                ) {

                    val formatter = SimpleDateFormat(
                        "hh:mm aa", Locale.US)
                    return if (date != null) {
                        formatter.format(date)
                    } else {
                        ""
                    }

                } else if (
                    ((calendarCurrent.get(Calendar.DATE) - calendar.get(Calendar.DATE)) == 1)
                    &&
                    ((calendarCurrent.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)))
                    &&
                    ((calendarCurrent.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)))
                ) {

//                    val formatter = SimpleDateFormat(
//                        "hh:mm aa", Locale.US)
//                    return if (date != null) {
//                        formatter.format(date)
//                    } else {
//                        ""
//                    }

                    return "Yesterday"
                } else {

                    val formatter =
                        SimpleDateFormat(
                            "MM/dd/yyyy",
                            Locale.US
                        )
                    return if (date != null) {
                        formatter.format(date)
                    } else {
                        ""
                    }
                }

            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                return ""
            }

        }

        fun getMessageTime(mMessageDate: String?): String? {
            //AppLogger.e("mMessageDate  $mMessageDate")

            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
            val calendar = Calendar.getInstance(Locale.US)
            val calendarCurrent = Calendar.getInstance(Locale.US)

            try {
                val date = sdf.parse(mMessageDate)
                calendar.time = date

                if (calendarCurrent.get(Calendar.DATE) == calendar.get(Calendar.DATE)
                    &&
                    ((calendarCurrent.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)))
                    &&
                    ((calendarCurrent.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)))
                ) {

                    val formatter = SimpleDateFormat(
                            "hh:mm aa", Locale.US)
                    return if (date != null) {
                        formatter.format(date)
                    } else {
                        ""
                    }

                } else if (
                    ((calendarCurrent.get(Calendar.DATE) - calendar.get(Calendar.DATE)) == 1)
                    &&
                    ((calendarCurrent.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)))
                    &&
                    ((calendarCurrent.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)))
                ) {

//                    val formatter = SimpleDateFormat(
//                        "hh:mm aa", Locale.US)
//                    return if (date != null) {
//                        formatter.format(date)
//                    } else {
//                        ""
//                    }

                    return "Yesterday"
                } else {

                    val formatter =
                        SimpleDateFormat(
                            "MM/dd/yyyy",
                            Locale.US
                        )
                    return if (date != null) {
                        formatter.format(date)
                    } else {
                        ""
                    }
                }

            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                return ""
            }

        }

        fun getMessageForTime(mMessageDate: String?): String? {
            var diffInDays: Long = 0
            var diffInHours: Long = 0
            var diffInSecond: Long = 0
            var diffInMinutes: Long = 0

            //AppLogger.e("mMessageDate  $mMessageDate")

            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
            val calendar = Calendar.getInstance(Locale.US)
            val date = calendar.time

            //2020-08-19 12:22:17
            try {
                val date1 = sdf.parse(mMessageDate)
                val date2 = sdf.parse(sdf.format(date))
                val diffInMilliSec = date2.time - date1.time
//                val diffInMilliSec = date2.time - date1.time
                diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMilliSec)
                diffInSecond = TimeUnit.MILLISECONDS.toSeconds(diffInMilliSec)
                diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMilliSec)
                diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMilliSec)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                return ""
            }

            AppLogger.e("diffInSecond $diffInSecond")

            when {
                diffInSecond < 60 -> {
                    return "Just now"
                }
                diffInMinutes < 60 -> {
                    if (diffInMinutes <= 1) {
                        return "$diffInMinutes min ago"
                    }
                    return "$diffInMinutes mins ago"
                }
                diffInHours < 24 -> {
                    if (diffInHours <= 1) {
                        return "$diffInHours hr ago"
                    }
                    return "$diffInHours hrs ago"
                }
                diffInDays == 1L -> {
                    return "$diffInDays day ago"
                }
                else -> return "$diffInDays days ago"
            }
        }

        fun getDifference(mNotificationDate: String?): String? {
            var diffInDays: Long = 0
            var diffInHours: Long = 0
            var diffInSecond: Long = 0
            var diffInMinutes: Long = 0

            AppLogger.e("mNotificationDate  $mNotificationDate")

            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            val calendar = Calendar.getInstance(Locale.US)
            val date = calendar.time

            //2018-07-13T05:08:01+00:00
//        2013-03-13T20:59:31+0000
            try {
                val date1 = sdf.parse(mNotificationDate)
                val date2 = sdf.parse(sdf.format(date))
                val diffInMilliSec = date2.time - date1.time
//                val diffInMilliSec = date2.time - date1.time
                diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMilliSec)
                diffInSecond = TimeUnit.MILLISECONDS.toSeconds(diffInMilliSec)
                diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMilliSec)
                diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMilliSec)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                return ""
            }

            AppLogger.e("diffInSecond $diffInSecond")

            when {
                diffInSecond < 60 -> {
                    return "Just now"
                }
                diffInMinutes < 60 -> {
                    if (diffInMinutes <= 1) {
                        return "$diffInMinutes min ago"
                    }
                    return "$diffInMinutes mins ago"
                }
                diffInHours < 24 -> {
                    if (diffInHours <= 1) {
                        return "$diffInHours hr ago"
                    }
                    return "$diffInHours hrs ago"
                }
                diffInDays == 1L -> {
                    return "$diffInDays day ago"
                }
                else -> return "$diffInDays days ago"
            }
        }

        fun getDifferenceDay(mNotificationDate: String?): String? {
            AppLogger.e("mNotificationDate  $mNotificationDate")

            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            val calendar = Calendar.getInstance(Locale.US)
            val calendarCurrent = Calendar.getInstance(Locale.US)

            try {
                val date = sdf.parse(mNotificationDate)
                calendar.time = date

                if (calendarCurrent.get(Calendar.DATE) == calendar.get(Calendar.DATE)
                    &&
                    ((calendarCurrent.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)))
                    &&
                    ((calendarCurrent.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)))
                ) {
                    return "Today"

                } else if (
                    ((calendarCurrent.get(Calendar.DATE) - calendar.get(Calendar.DATE)) == 1)
                    &&
                    ((calendarCurrent.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)))
                    &&
                    ((calendarCurrent.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)))
                ) {
                    return "Yesterday"
                } else {
                    return getDateFromServerTime(mNotificationDate ?: "")
                }

            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                return ""
            }

        }

    }

    fun getCompareYearMonth(date1: Date, date: Date): Boolean {
        val sdf = SimpleDateFormat("yyyy-MM", Locale.US)
        return sdf.format(date) == sdf.format(date1)
    }

    fun getWeekend(time: String): Boolean {
        val input = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val output = SimpleDateFormat("EEEE", Locale.US)
        var date: Date? = null
        var str = ""
        try {
            date = input.parse(time)
            if (date != null)
                str = output.format(date) ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
        }

//        AppLogger.e("time Date  $time")
//        AppLogger.e("str  $str")

        val isWeekend = (str == "Sunday" || str == "Saturday")
        return if (isWeekend) {
            true
        } else {
            if (date == null) {
                return false
            } else {
                val auxCalendar = Calendar.getInstance(Locale.US)
                val auxCalendar1 = Calendar.getInstance(Locale.US)
                auxCalendar1.time = date
                if (((auxCalendar[Calendar.YEAR] == auxCalendar1[Calendar.YEAR]) && (auxCalendar[Calendar.MONTH] == auxCalendar1[Calendar.MONTH]) && (auxCalendar[Calendar.DAY_OF_MONTH] == auxCalendar1[Calendar.DAY_OF_MONTH]))) {
                    return false
                } else {
                    date < auxCalendar.time
                }
            }
        }

    }

    fun getWeekendFromDate(date: Date): Boolean {

        val sdf = SimpleDateFormat("EEEE", Locale.US)
        val str = sdf.format(date)
//        AppLogger.e("str  $str")

        return (str == "Sunday" || str == "Saturday")

    }

    fun getWeekendAndPreviousDate(date: Date): Boolean {

        val sdf = SimpleDateFormat("EEEE", Locale.US)
        val str = sdf.format(date)
        AppLogger.e("str  $str")

        val isWeekend = (str == "Sunday" || str == "Saturday")
        return if (isWeekend) {
            true
        } else {
            val auxCalendar = Calendar.getInstance(Locale.US)
            val auxCalendar1 = Calendar.getInstance(Locale.US)
            auxCalendar1.time = date
            if (((auxCalendar[Calendar.YEAR] == auxCalendar1[Calendar.YEAR]) && (auxCalendar[Calendar.MONTH] == auxCalendar1[Calendar.MONTH]) && (auxCalendar[Calendar.DAY_OF_MONTH] == auxCalendar1[Calendar.DAY_OF_MONTH]))) {
                return false
            } else {
                date < auxCalendar.time
            }
        }
    }

}