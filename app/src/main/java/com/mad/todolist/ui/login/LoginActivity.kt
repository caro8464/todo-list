package com.mad.todolist.ui.login

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.MAD.todolist.R
import com.mad.todolist.todo.MainTasksActivity
import android.content.Intent
import android.net.ConnectivityManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import java.lang.Thread.sleep

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()

        if (!verifyAvailableNetwork(this@LoginActivity)) {
            Toast.makeText(
                applicationContext,
                "Warning: no Connection, falling back to Device Storage",
                Toast.LENGTH_LONG
            ).show()
//            finish()
            intent = Intent(this@LoginActivity, MainTasksActivity::class.java)
            intent.putExtra("email", "offline")
            intent.putExtra("logged_in", true)
            startActivity(intent)
        }

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val login = findViewById<Button>(R.id.login)
        val loading = findViewById<ProgressBar>(R.id.loading)

        loginViewModel = ViewModelProviders.of(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer
            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
            if (loginState.authentificationError != null) {
                login.error = getString(loginState.authentificationError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
            setResult(Activity.RESULT_OK)

            //Complete and destroy login activity once successful
//            finish()
        })
        username.apply {

            username.setOnFocusChangeListener { _, b ->
                if (!b) {
                    loginViewModel.loginDataChanged(
                        username.text.toString(),
                        password.text.toString(),
                        true
                    )
                }
            }
            afterTextChanged { login.error = "FAIL" }
        }
        password.apply {
            setOnFocusChangeListener { _, b ->
                if (!b) {
                    loginViewModel.loginDataChanged(
                        username.text.toString(),
                        password.text.toString(),
                        true
                    )
                }
            }
            afterTextChanged { login.isEnabled = true }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        if (isUserNameValid(username.text.toString()) && isPasswordValid(password.text.toString())) {
                            loading.visibility = View.VISIBLE
                            plzAuthMeFirebase(username.text.toString(), password.text.toString())
                        } else {
                            loginViewModel.loginDataChanged(
                                username.text.toString(),
                                password.text.toString(),
                                true
                            )
                        }
                    }
                }
                false
            }
            login.setOnClickListener {
                if (isUserNameValid(username.text.toString()) && isPasswordValid(password.text.toString())) {
                    loading.visibility = View.VISIBLE
                    plzAuthMeFirebase(username.text.toString(), password.text.toString())
                } else {
                    loginViewModel.loginDataChanged(
                        username.text.toString(),
                        password.text.toString(),
                        true
                    )
                }
            }
        }
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
        intent = Intent(this@LoginActivity, MainTasksActivity::class.java)
        intent.putExtra("email", model.displayName)
        intent.putExtra("logged_in", true)
        startActivity(intent)
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }

    private fun verifyAvailableNetwork(activity: AppCompatActivity): Boolean {
        val connectivityManager = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun plzAuthMeFirebase(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    sleep(2000)
                    loginViewModel.login(email, password)
                    loading.visibility = View.GONE
                } else {
                    sleep(2000)
                    loginViewModel.loginDataChanged(email, password, false)
                    loading.visibility = View.GONE
                }
            }
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}

// A placeholder username validation check
private fun isUserNameValid(username: String): Boolean {
    return username.contains('@') && Patterns.EMAIL_ADDRESS.matcher(username).matches()
    /*
    return if (username.contains('@')) {
        Patterns.EMAIL_ADDRESS.matcher(username).matches()
    } else {
        false
    }
    */
}

// A placeholder password validation check
private fun isPasswordValid(password: String): Boolean {
    val regex: Regex = "^[0-9]+$".toRegex()
    return password.length == 6 && password.matches(regex)
}

