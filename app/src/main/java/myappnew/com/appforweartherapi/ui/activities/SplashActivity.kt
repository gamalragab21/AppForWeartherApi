package myappnew.com.appforweartherapi.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import myappnew.com.appforweartherapi.R
import myappnew.com.appforweartherapi.helper.SessionManager
import myappnew.com.appforweartherapi.ui.viewmodels.SettingsViewModel
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels()
    @Inject
    lateinit var sessionManager : SessionManager
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splach)
        settingsViewModel.langStatus.observe(this ,{lang->
            when(lang){
                "ar"->{
                    sessionManager.setLocal("ar",this)
                }
                "en"->{
                    sessionManager.setLocal("en" , this )
                }
                else->{
                    sessionManager.setLocal("en" , this)
                }
            }

        })
        startActivity(Intent(this@SplashActivity , MainActivity::class.java))
        finish()


    }
}