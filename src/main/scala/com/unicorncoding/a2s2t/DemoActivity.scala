package com.unicorncoding.a2s2t

import akka.actor.ActorSystem
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import com.typesafe.config.ConfigFactory
import com.unicorncoding.a2s2t.fragments.DemoFragment

class DemoActivity extends Activity
  with AkkaUtils
  with AndroidUtils {

  override def TAG: String = "DemoActivity"

  override val actorSystemName: String = "zookeeper"

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main)

    initDemoFragment()
  }

  override def onBackPressed(): Unit = {
    actorSystem.shutdown()

    super.onBackPressed()
  }

  def initDemoFragment(): Unit = {
    val demoFragment = DemoFragment(this)
    getFragmentManager
      .beginTransaction()
      .add(R.id.fragment_container, demoFragment)
      .commit()
  }

  def execOnUi(f: => Unit): Unit = {
    runOnUiThread(new Runnable {
      override def run(): Unit = f
    })
  }
}

trait AkkaUtils {
  self: Activity ⇒
  val actorSystemName: String
  lazy val actorSystem = ActorSystem(
    actorSystemName,
    ConfigFactory.load(getApplication.getClassLoader),
    getApplication.getClassLoader
  )
}

trait AndroidUtils {

  def TAG: String

  def logInfo(msg: String): Unit = {
    Log.i(TAG, msg)
  }

  def logWarn(msg: String): Unit = {
    Log.w(TAG, msg)
  }

  def logError(msg: String): Unit = {
    Log.e(TAG, msg)
  }

  def mkOnClickListener(f: => Unit): OnClickListener = {
    new OnClickListener {
      override def onClick(v: View): Unit = f
    }
  }

  def mkToast(context: Context, msg: String, length: Int = Toast.LENGTH_SHORT): Toast = {
    Toast.makeText(context, msg, length)
  }

}
