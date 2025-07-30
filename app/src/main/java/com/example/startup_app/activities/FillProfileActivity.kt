package com.example.startup_app.activities

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.method.TextKeyListener
import android.util.Patterns
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.startup_app.R
import com.example.startup_app.databinding.ActivityFillProfileBinding
import com.example.startup_app.viewmodels.User
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar
import java.util.Locale

class FillProfileActivity : BaseActivity() {

    private lateinit var binding: ActivityFillProfileBinding
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private var selectedImageUri: Uri? = null
    private lateinit var userData : User
    @SuppressLint("DefaultLocale")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFillProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sign = intent.getBooleanExtra("sign", false)
        val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        val userDataJson = intent.getStringExtra("userInfo")
        val gson = Gson()
        val user: User? = if (userDataJson != null) {
            gson.fromJson(userDataJson, User::class.java)
        } else {
            null
        }
        if (user != null) {
            binding.emailinfo.text = Editable.Factory.getInstance().newEditable(if (user.email == "null") "" else user.email)
            binding.lnameinfo.text = Editable.Factory.getInstance().newEditable(if (user.lname == "null") "" else user.lname)
            binding.fnameinfo.text = Editable.Factory.getInstance().newEditable(if (user.fname == "null") "" else user.fname)
            binding.genderinfo.setText(if (user.gender == "null") "" else user.gender, false)
            binding.birthinfo.setText(if (user.birthday == "null") "" else user.birthday)
            if(!sign) {
                binding.genderinfo.isEnabled = false
                binding.genderinfo.isClickable = false
                binding.genderinfo.isFocusable = false
                binding.genderinfo.isCursorVisible = false
                binding.birthinfo.keyListener = null
                binding.birthinfo.isClickable = false
                binding.birthinfo.isFocusable = false
                binding.birthinfo.isEnabled = false

                binding.emailinfo.isEnabled = true
                binding.emailinfo.isFocusable = true
                binding.emailinfo.isFocusableInTouchMode = true
                binding.emailinfo.isClickable = true
                binding.emailinfo.isCursorVisible = true
                binding.emailinfo.keyListener = TextKeyListener.getInstance()
            }
            val defaultImageRes = R.drawable.user1

            try {
                if (sign) {
                    if (user.image != "null") {
                        val uri = Uri.parse(user.image)
                        binding.photoinfo.setImageURI(uri)
                    } else {
                        binding.photoinfo.setImageResource(defaultImageRes)
                    }
                } else {
                    if (user.image != "null") {
                        val uri = Uri.parse(user.image)
                        binding.photoinfo.setImageURI(uri)
                    } else {
                        binding.photoinfo.setImageResource(defaultImageRes)
                    }
                }
            } catch (e: Exception) {
                binding.photoinfo.setImageResource(defaultImageRes)
            }



        }

        binding.back.setOnClickListener {
            if(sign) {
                startActivity(
                    Intent(this@FillProfileActivity, SignUpActivity::class.java),
                    ActivityOptions.makeCustomAnimation(
                        this,
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                    ).toBundle()
                )
            } else {
                startActivity(
                    Intent(this@FillProfileActivity, ProfileActivity::class.java),
                    ActivityOptions.makeCustomAnimation(
                        this,
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                    ).toBundle()
                )
            }

        }

        val dateInput = binding.birthinfo
        dateInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, y, m, d ->
                dateInput.setText(String.format("%02d/%02d/%04d", d, m + 1, y))
            }, year, month, day)

            datePicker.show()
        }

        val gender = binding.genderinfo
        val genderOptions = listOf("Male", "Female")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, genderOptions)
        gender.setAdapter(adapter)
        gender.setOnClickListener {
            gender.alpha = 0f
            gender.animate().alpha(1f).setDuration(200).start()
            Handler(Looper.getMainLooper()).postDelayed({
                gender.showDropDown()
            }, 150)
        }

        val photo = binding.photoinfo
        val edit = binding.photoedit
        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val imageUri = result.data?.data
                    if (imageUri != null) {
                        val copiedUri = copyUriToInternalStorage(imageUri)
                        selectedImageUri = copiedUri
                        photo.setImageURI(copiedUri)
                    }
                }
            }

        edit.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }
            pickImageLauncher.launch(intent)
        }

        binding.saveinfo.setOnClickListener {
            val birth = dateInput.text.toString()
            val lname = binding.lnameinfo.text.toString()
            val fname = binding.fnameinfo.text.toString()
            val gndr = gender.text.toString()
            val currentLang = Locale.getDefault().language
            val isFrench = currentLang == "fr"
            if (birth.isBlank() || lname.isBlank() || fname.isBlank() || gndr.isBlank() || binding.emailinfo.text.toString().isBlank() || !Patterns.EMAIL_ADDRESS.matcher(binding.emailinfo.text.toString()).matches()) {
                val message = if (isFrench) {
                    "Veuillez remplir tous les champs"
                } else {
                    "Please fill in all fields"
                }
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val defaultUri = Uri.parse("android.resource://${packageName}/drawable/user1")
            val imageUriString = selectedImageUri?.toString()
                ?: if (!user?.image.isNullOrEmpty() && user?.image != "null") user?.image
                else defaultUri.toString()

            if(sign) {
                userData = User(
                    lname = lname,
                    fname = fname,
                    username = user?.username ?: "",
                    email = user?.email ?: "",
                    birthday = birth,
                    gender = gndr,
                    image = imageUriString!!,
                    password = user?.password ?: "",
                )
            }  else {
                userData = User(
                    lname = lname,
                    fname = fname,
                    username = user?.username ?: "",
                    email = binding.emailinfo.text.toString(),
                    birthday = birth,
                    gender = gndr,
                    image = imageUriString!!,
                    password = user?.password ?: "",
                )
            }


            val json = gson.toJson(userData)
            editor.putString("userData", json)
            editor.remove("user_data")
            editor.apply()
            if(!sign){
                binding.successtext.text = if (isFrench)
                    "Vos informations de compte ont été mises à jour avec succès."
                else
                    "Your account information has been successfully updated."

                binding.successbigtext.text = if (isFrench) "Effectué" else "Done"

            }
            binding.lan.setBackgroundColor(Color.DKGRAY)
            binding.success.visibility = View.VISIBLE
            val elementsToHide = listOf(binding.signupform, binding.photoedit, binding.photoinfo)
            elementsToHide.forEach { it.visibility = View.GONE }
            binding.success.translationZ = 50f
            binding.lan.translationZ = 10f

            startRotation()
            Handler(Looper.getMainLooper()).postDelayed({
                val i = Intent(this@FillProfileActivity, HomeActivity::class.java)
                startActivity(
                    i,
                    ActivityOptions.makeCustomAnimation(this, android.R.anim.fade_in, android.R.anim.fade_out).toBundle()
                )
                finish()
                editor.remove("profilenotcompleted")
                editor.putBoolean("signed", true)
                editor.remove("sign")
                editor.apply()
            }, 5000)
        }
    }


    @SuppressLint("DefaultLocale")

    private fun startRotation() {
        val animator = ValueAnimator.ofFloat(0f, 360f).apply {
            duration = 3000
            interpolator = LinearInterpolator()
            addUpdateListener { binding.prgres.rotation = it.animatedValue as Float }
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    Handler(Looper.getMainLooper()).postDelayed({ startRotation() }, 2000)
                }
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
        }
        animator.start()
    }

    private fun copyUriToInternalStorage(uri: Uri): Uri? {
        return try {
            val input = contentResolver.openInputStream(uri)
            val file = File(filesDir, "profile_image.jpg")
            FileOutputStream(file).use { output -> input?.copyTo(output) }
            Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
