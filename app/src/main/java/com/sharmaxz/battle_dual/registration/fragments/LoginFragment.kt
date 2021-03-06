package com.sharmaxz.battle_dual.registration.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.sharmaxz.battle_dual.R
import com.sharmaxz.battle_dual.model.User
import com.sharmaxz.battle_dual.registration.RegistrationActivity
import com.sharmaxz.battle_dual.service.TokenService
import com.sharmaxz.battle_dual.service.UserService
import com.sharmaxz.battle_dual.settings.AppPreferences
import com.sharmaxz.battle_dual.shared.SessionManager
import kotlinx.android.synthetic.main.fragment_login_form.*

import kotlinx.coroutines.*

class LoginFragment : Fragment() {

    var timeout = false
    var error = false

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view  = inflater.inflate(R.layout.fragment_login_form, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity!!.windowManager.defaultDisplay.getRealMetrics(resources.displayMetrics)
        btnLogin.setOnClickListener { attemptLogin() }
        btnRegistration.setOnClickListener {
            (activity as RegistrationActivity).registration()
        }
    }


    private fun attemptLogin() {
        //Avoid other login attempt
        lock()

        // Reset errors.
        txtError.visibility = View.GONE
        email.error = null
        password.error = null

        // Store values at the time of the login attempt.
        val emailStr = email.text.toString()
        val passwordStr = password.text.toString()

        var cancel = false
        var focusView : View? = null

        if (TextUtils.isEmpty(passwordStr)) {
            password.error = getString(R.string.login_error_password_empty)
            focusView = password
            cancel = true
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(emailStr)) {
            email.error = getString(R.string.login_error_email_empty)
            focusView = email
            cancel = true
        }
//        } else if (!isEmailValid(emailStr)) {
//            email.error = getString(R.string.login_error_email)
//            focusView = email
//            cancel = true
//        }

        if (!cancel) {
            focusView = null
            GlobalScope.launch {
                supervisorScope {
                    login(emailStr, passwordStr)
                }
            }
        } else {
            unlock()
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return email.contains("@")
    }

    private fun login(nickname : String, password : String) {
        val r =  TokenService.post(nickname, password)

        if(r["response"] == 200) {
            val user = UserService.get(nickname=nickname)

            if (user::class.java.simpleName != "User") {
                timeout()
                unlock()
            } else {
                SessionManager.create(user as User)
                (activity as RegistrationActivity).openCreation()
            }
        }
        else if(r["response"] == 404){
            error()
            unlock()
        }
        else if(r["response"] == 0 || r["response"] == 400) {
            timeout()
            unlock()
        }
    }

    private fun lock() {
        activity?.runOnUiThread(Runnable {
            email.isEnabled = false
            password.isEnabled = false
            btnLogin.isEnabled = false
            btnRegistration.isEnabled = false
            txtError.visibility = View.GONE
        })
    }

    private fun unlock() {
        activity?.runOnUiThread(Runnable {
            email.isEnabled = true
            password.isEnabled = true
            btnLogin.isEnabled = true
            btnRegistration.isEnabled = true
        })
    }

    private fun timeout() {
        activity?.runOnUiThread(Runnable {
            txtError.visibility = View.VISIBLE
            txtError.text = getString(R.string.login_error_timeout)
        })
    }

    private fun error() {
        activity?.runOnUiThread(Runnable {
            txtError.visibility = View.VISIBLE
            txtError.text = getString(R.string.login_error)
        })
    }
}
