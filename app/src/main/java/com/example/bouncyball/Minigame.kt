package com.example.bouncyball

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp


@Composable
fun BouncyBallGame() {
    val context = LocalContext.current
    val sharedPref: SharedPreferences = context.getSharedPreferences("game_prefs", Context.MODE_PRIVATE)
    val highScore = sharedPref.getInt("high_score", 0)

    //Ball Data
    var ballY by remember { mutableFloatStateOf(200f) } // Ball's Y position
    var ballX by remember { mutableFloatStateOf(150f) } // Ball's X position
    var ballVelocityY by remember { mutableFloatStateOf(0f) } // Ball's vertical velocity
    var ballOnPlatform by remember { mutableStateOf(false) } // On platform boolean

    // Platform Data
    var platformX by remember { mutableFloatStateOf(100f) } //horizontal position
    var platformWidth by remember { mutableFloatStateOf(200f) } //width
    var platformSpeed by remember { mutableFloatStateOf(2f) } //speed
    val platformY = 500f //vertical position always the same

    //Other Game Data
    var score by remember { mutableIntStateOf(0) }
    val gravity = 1f //how fast ball falls
    val bounceStrength = -20f //how much ball bounces off platform

    // Game started flag and game over flag
    var gameStarted by remember { mutableStateOf(false) }
    var gameOver by remember { mutableStateOf(false) }

    // Handle the game loop
    if (gameStarted && !gameOver) {

        LaunchedEffect(gameStarted) {
            kotlinx.coroutines.delay(2000)

            // Start falling immediately
            ballVelocityY = gravity
            while (gameStarted && !gameOver) {//while the game is started and not over

                // Platform collision logic
                if (ballY + 75f >= platformY && ballY + 75f <= platformY + 10f
                    && ballX + 50f > platformX && ballX < platformX + platformWidth
                    && ballVelocityY > 0)

                {//if in contact with platform set velocity to be bounce strength
                    ballVelocityY = bounceStrength

                    //if the platform is moving increase the speed, (inverse for going left)
                    if (platformSpeed > 0) {
                        platformSpeed += 0.2f
                    } else {
                        platformSpeed -= 0.2f
                    }


                    //Make platform smaller till 50f
                    if (platformWidth > 50f) {
                        platformWidth -= 2f
                    }
                    score++
                }

                // Boolean for checking if the ball is on a platform
                if (ballY + 75f < platformY || ballY + 75f > platformY + 10f) {
                    ballOnPlatform = false
                }

                //if not on platform apply the gravity to pull it down
                if (!ballOnPlatform) {
                    ballVelocityY += gravity
                    ballY += ballVelocityY
                }

                //game over if you fall offscreen
                if (ballY > 1000f) {
                    gameOver = true
                }

                //move platform at whatever speed, if it hits the edge reverse it
                platformX += platformSpeed
                if (platformX <= 0f || platformX >= 390f - platformWidth) {
                    platformSpeed = - platformSpeed
                }
                kotlinx.coroutines.delay(16)
            }
        }
    }



    //VISUALS
    Box(Modifier.padding())
    {
        // Show the game screen only if the game has started and is not over
        if (gameStarted && !gameOver) {
            Box(modifier = Modifier.fillMaxSize()) {

                // Ball Image
                Box(modifier = Modifier.offset(x = ballX.dp, y = ballY.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.ball),
                        contentDescription = "Ball",
                        modifier = Modifier.size(90.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                // moving platform with dynamic width
                Box(
                    modifier = Modifier
                        .offset(x = platformX.dp, y = platformY.dp)
                        .background(Color.Gray)
                        .size(platformWidth.dp, 10.dp)
                )



                //SCORE DISPLAY START
                Card(
                    Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 40.dp)
                        .fillMaxWidth(0.7f)
                        .height(50.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Score: $score   High Score: $highScore"
                        )
                    }
                }
                //SCORE DISPLAY END
            }



            // SLIDER START
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(20.dp, bottom = 50.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Slider(
                    value = ballX / 300f,
                    onValueChange = { newValue ->
                        ballX = newValue * 300f
                    },
                    valueRange = 0f..1f,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.onPrimary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.primary,
                    )
                )
            }
            // SLIDER END



            //GAME OVER SCREEN START
        } else if (gameOver) {
            val savedHighScore = sharedPref.getInt("high_score", 0)
            val isNewHighScore = score > savedHighScore

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if (isNewHighScore)
                {
                    sharedPref.edit().putInt("high_score", score).apply()
                }

                Button(
                    onClick = {
                        // Reset the game state and start again
                        score = 0
                        ballY = 200f
                        ballX = 150f
                        ballVelocityY = gravity
                        ballOnPlatform = false
                        platformX = 100f
                        platformWidth = 200f
                        platformSpeed = 2f
                        gameStarted = true
                        gameOver = false }
                )
                {
                    Text(text = "Play Again")
                }
            }
        }


        else {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Button(
                    onClick = {
                        gameStarted = true
                        gameOver = false
                    },
                ) {
                    Text(text = "Play")
                }
            }
        }
    }
}




