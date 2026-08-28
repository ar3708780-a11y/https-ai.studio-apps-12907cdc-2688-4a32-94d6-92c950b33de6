package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.CommentEntity
import com.example.data.model.UserEntity
import com.example.data.model.VideoEntity
import com.example.ui.components.VideoPlayerModal
import com.example.ui.screens.ProfileScreen
import com.example.ui.theme.DoDuTheme
import com.example.ui.viewmodel.ProfileTab
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Do+Du", appName)
  }

  @Test
  fun `verify user profile model structure`() {
    val user = UserEntity(
      id = "user_test_1",
      username = "cyber_ace",
      displayName = "Cyber Ace",
      bio = "Radiant ranked FPS streamer. Sharing daily clutches!",
      avatarDrawableName = "img_avatar_gamer",
      customAvatarUri = null,
      favoriteGame = "Apex Cyberzone",
      gamingRank = "Radiant / Master",
      followersCount = 12500,
      followingCount = 180,
      totalLikesCount = 42000,
      isCurrentUser = false
    )

    assertEquals("cyber_ace", user.username)
    assertEquals("Cyber Ace", user.displayName)
    assertNotNull(user.avatarDrawableName)
    assertEquals("Radiant ranked FPS streamer. Sharing daily clutches!", user.bio)
  }

  @Test
  fun `profile screen displays username and bio`() {
    val user = UserEntity(
      id = "user_test_2",
      username = "neon_valkyrie",
      displayName = "Neon Valkyrie",
      bio = "Esports caster & clutch queen",
      avatarDrawableName = "img_avatar_gamer",
      customAvatarUri = null,
      favoriteGame = "Cyberpunk Arena",
      gamingRank = "Grandmaster",
      followersCount = 89000,
      followingCount = 210,
      totalLikesCount = 15000,
      isCurrentUser = true
    )

    val testVideo = VideoEntity(
      id = "v1",
      creatorId = "user_test_2",
      title = "1v5 Insane Clutch Ace",
      description = "Unbelievable pistol round clutch",
      gameTitle = "Cyberpunk Arena",
      thumbnailDrawableName = "img_thumb_fps",
      duration = "0:45",
      isClip = true,
      viewsCount = 14200,
      likesCount = 1200,
      isLiked = false
    )

    composeTestRule.setContent {
      DoDuTheme(darkTheme = true) {
        ProfileScreen(
          user = user,
          isCurrentUser = true,
          allCreators = listOf(user),
          uploadedVideos = listOf(testVideo),
          likedVideos = emptyList(),
          selectedTab = ProfileTab.UPLOADS,
          onTabSelected = {},
          onPlayVideo = {},
          onToggleLike = {},
          onDeleteVideo = {},
          onSaveProfile = { _, _, _, _, _, _ -> },
          onUploadVideo = { _, _, _, _, _, _ -> },
          onSwitchUser = {},
          onBackToMyProfile = {}
        )
      }
    }

    // Verify username and bio are rendered
    composeTestRule.onNodeWithTag("profile_username_text").assertIsDisplayed()
    composeTestRule.onNodeWithText("@neon_valkyrie").assertIsDisplayed()
    composeTestRule.onNodeWithTag("profile_bio_text").assertIsDisplayed()
    composeTestRule.onNodeWithText("Esports caster & clutch queen").assertIsDisplayed()
  }

  @Test
  fun `verify CommentEntity model hierarchy with parent comment and replies`() {
    val rootComment = CommentEntity(
      id = "c_1",
      videoId = "v_101",
      userId = "u_pixel",
      authorName = "Kai Storm",
      authorUsername = "pixel_ninja",
      authorAvatarDrawable = "img_creator_avatar",
      authorRank = "World Record Holder",
      content = "Insane clutch! What sens are you running?",
      timestamp = 1000L,
      parentCommentId = null,
      replyToUsername = null,
      likesCount = 5,
      isLiked = true
    )

    val replyComment = CommentEntity(
      id = "r_1",
      videoId = "v_101",
      userId = "u_primary",
      authorName = "Valkyrie Ray",
      authorUsername = "neon_valkyrie",
      authorAvatarDrawable = "img_avatar_gamer",
      authorRank = "Apex Predator #42",
      content = "Thanks Kai! 800 DPI, 1.2 in-game.",
      timestamp = 1100L,
      parentCommentId = "c_1",
      replyToUsername = "pixel_ninja",
      likesCount = 3,
      isLiked = false
    )

    assertNull(rootComment.parentCommentId)
    assertEquals("c_1", replyComment.parentCommentId)
    assertEquals("pixel_ninja", replyComment.replyToUsername)
    assertEquals("v_101", rootComment.videoId)
    assertEquals(replyComment.videoId, rootComment.videoId)
    assertTrue(rootComment.isLiked)
  }

  @Test
  fun `video player modal renders comments, replies, and reply affordance`() {
    val creator = UserEntity(
      id = "u_creator",
      username = "neon_valkyrie",
      displayName = "Neon Valkyrie",
      bio = "Esports caster",
      avatarDrawableName = "img_avatar_gamer",
      customAvatarUri = null,
      favoriteGame = "FPS",
      gamingRank = "Radiant",
      followersCount = 50000,
      followingCount = 100,
      totalLikesCount = 20000,
      isCurrentUser = false
    )

    val currentUser = UserEntity(
      id = "u_primary",
      username = "kai_storm",
      displayName = "Kai Storm",
      bio = "Speedrunner",
      avatarDrawableName = "img_creator_avatar",
      customAvatarUri = null,
      favoriteGame = "FPS",
      gamingRank = "Champion",
      followersCount = 12000,
      followingCount = 80,
      totalLikesCount = 10000,
      isCurrentUser = true
    )

    val video = VideoEntity(
      id = "v_101",
      creatorId = "u_creator",
      title = "Tournament Clutch Finals",
      description = "Final defuse play",
      gameTitle = "Cyber Arena",
      thumbnailDrawableName = "img_thumb_fps",
      duration = "1:20",
      isClip = true,
      viewsCount = 12000,
      likesCount = 850,
      isLiked = false
    )

    val comments = listOf(
      CommentEntity(
        id = "c_101_1",
        videoId = "v_101",
        userId = "u_primary",
        authorName = "Kai Storm",
        authorUsername = "kai_storm",
        authorAvatarDrawable = "img_creator_avatar",
        authorRank = "Champion",
        content = "What an unbelievable defuse!!",
        timestamp = System.currentTimeMillis() - 60000L,
        parentCommentId = null,
        replyToUsername = null,
        likesCount = 7,
        isLiked = true
      ),
      CommentEntity(
        id = "r_101_1_1",
        videoId = "v_101",
        userId = "u_creator",
        authorName = "Neon Valkyrie",
        authorUsername = "neon_valkyrie",
        authorAvatarDrawable = "img_avatar_gamer",
        authorRank = "Radiant",
        content = "Heart was racing the entire round!",
        timestamp = System.currentTimeMillis() - 30000L,
        parentCommentId = "c_101_1",
        replyToUsername = "kai_storm",
        likesCount = 4,
        isLiked = false
      )
    )

    var postedContent: String? = null
    var postedParentId: String? = null

    composeTestRule.setContent {
      DoDuTheme(darkTheme = true) {
        VideoPlayerModal(
          video = video,
          creator = creator,
          currentUser = currentUser,
          comments = comments,
          onDismiss = {},
          onToggleLike = {},
          onCreatorClick = {},
          onPostComment = { content, parentId, _ ->
            postedContent = content
            postedParentId = parentId
          }
        )
      }
    }

    // Verify comments section is displayed
    composeTestRule.onNodeWithTag("comments_section").assertIsDisplayed()
    composeTestRule.onNodeWithTag("comment_card_c_101_1").assertIsDisplayed()
    composeTestRule.onNodeWithText("What an unbelievable defuse!!").assertIsDisplayed()
    composeTestRule.onNodeWithText("Heart was racing the entire round!").assertIsDisplayed()

    // Test clicking the Reply button on comment
    composeTestRule.onNodeWithTag("reply_to_c_101_1").performClick()

    // Verify reply banner appears
    composeTestRule.onNodeWithTag("cancel_reply_button").assertIsDisplayed()
    composeTestRule.onNodeWithText("Replying to @kai_storm").assertIsDisplayed()
  }
}
