package com.acacia.seventodo.todo.setting

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.acacia.seventodo.*
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import kotlinx.android.synthetic.main.fragment_todo_setting.*

class TodoSettingFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_todo_setting, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        requireActivity().window.statusBarColor = Color.TRANSPARENT

        setFm_tv_version.text = "version ${BuildConfig.VERSION_NAME}"

        setFm_btn_back.setOnClickListener {
            findNavController().popBackStack()
        }

        setFm_btn_policy.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(resources.getString(R.string.policy_url))
            startActivity(intent)
        }

        setFm_btn_license.setOnClickListener {
            startActivity(Intent(requireContext(), OssLicensesMenuActivity::class.java))
        }

        setFm_btn_notiBar.setOnClickListener {
            val intent = Intent(requireContext(), NotifyActionReceiver::class.java)
            intent.action = "REFRESH"
            requireContext().sendBroadcast(intent)
        }
    }
}