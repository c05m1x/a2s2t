package com.unicorncoding.a2s2t.fragments

import akka.actor.{Actor, ActorRef, Props}
import android.app.{Activity, Fragment}
import android.os.Bundle
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.{TextView, Button, ImageView, Toast}
import com.unicorncoding.a2s2t.{AndroidUtils, DemoActivity, R}

class DemoFragment extends Fragment
  with AndroidUtils
  with View.OnClickListener {

  override def TAG: String = "DemoFragment"

  lazy val actionShow: String = getResources.getString(R.string.btn_action_show)
  lazy val actionHide: String = getResources.getString(R.string.btn_action_hide)
  lazy val ctx = this.getActivity.asInstanceOf[DemoActivity]

  var imgAkka: ImageView = _
  var imgSbt: ImageView = _
  var imgScala: ImageView = _
  var txtInfo: TextView = _
  var btnAction: Button = _
  var startVisualisation: Boolean = true
  var demoActor: ActorRef = _

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    val v = inflater.inflate(R.layout.fragment_demo, container, false)
    demoActor = ctx.actorSystem.actorOf(Props(DemoActor(ctx)))
    v
  }

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    super.onViewCreated(view, savedInstanceState)

    initViews()
  }

  override def onClick(v: View): Unit = {
    v.getId match {
      case R.id.btn_action =>
        switchAction()
      case _ =>
        ctx.execOnUi(Toast.makeText(ctx, "Unhandled view click.", Toast.LENGTH_LONG).show())
    }
  }

  def initViews(): Unit = {
    btnAction = ctx.findViewById(R.id.btn_action).asInstanceOf[Button]
    btnAction.setText(actionShow)
    btnAction.setOnClickListener(this)
    imgAkka = ctx.findViewById(R.id.img_akka).asInstanceOf[ImageView]
    imgAkka.setVisibility(View.GONE)
    imgSbt = ctx.findViewById(R.id.img_sbt).asInstanceOf[ImageView]
    imgSbt.setVisibility(View.GONE)
    imgScala = ctx.findViewById(R.id.img_scala).asInstanceOf[ImageView]
    imgScala.setVisibility(View.GONE)
    txtInfo = ctx.findViewById(R.id.txt_info).asInstanceOf[TextView]
    txtInfo.setVisibility(View.GONE)
  }

  def switchAction(): Unit = {
    if (startVisualisation) {
      btnAction.setText(actionHide)
      demoActor ! StartVisualization
    }
    else {
      btnAction.setText(actionShow)
      demoActor ! StopVisualization
    }
    startVisualisation = !startVisualisation
  }

  sealed trait DemoMessage

  case object StartVisualization extends DemoMessage

  case object ResetInterruptState extends DemoMessage

  case object StopVisualization extends DemoMessage

  case class DemoActor(parent: Activity) extends Actor {
    var interruptCall = false
    val activity = parent.asInstanceOf[DemoActivity]

    override def receive: Receive = {
      case StartVisualization =>
        if (!interruptCall) {
          visualizationSequence(500)
          self ! StartVisualization
        }
      case StopVisualization =>
        interruptCall = true
        activity.execOnUi({
          txtInfo.setText("")
          txtInfo.setVisibility(View.GONE)
        })
        self ! ResetInterruptState
      case ResetInterruptState =>
        interruptCall = false
    }

    def visualizationSequence(delayInMs: Int): Unit = {
      // Thread.sleep is a no go in production - this is solely for demonstration purpose!!!
      activity.execOnUi({
        txtInfo.setVisibility(View.VISIBLE)
        imgAkka.setVisibility(View.VISIBLE)
        txtInfo.setText("akka 2.3.14")
      })

      Thread.sleep(delayInMs)

      activity.execOnUi({
        imgAkka.setVisibility(View.GONE)
        imgSbt.setVisibility(View.VISIBLE)
        txtInfo.setText("sbt 0.13.8")
      })

      Thread.sleep(delayInMs)

      activity.execOnUi({
        imgSbt.setVisibility(View.GONE)
        imgScala.setVisibility(View.VISIBLE)
        txtInfo.setText("scala 2.11.8")
      })

      Thread.sleep(delayInMs)

      activity.execOnUi(imgScala.setVisibility(View.GONE))
    }
  }

}