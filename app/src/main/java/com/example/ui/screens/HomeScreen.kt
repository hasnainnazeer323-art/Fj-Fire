package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.entity.TournamentEntity
import com.example.ui.components.EsportsCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun HomeScreen(
  tournaments: List<TournamentEntity>,
  appliedTournamentIds: Set<String>,
  onTournamentClick: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedCategory by remember { mutableStateOf("All Tournaments") }
  val categories = listOf("All Tournaments", "Solo", "Squad", "Custom")

  val filteredTournaments = remember(selectedCategory, tournaments) {
    if (selectedCategory == "All Tournaments") {
      tournaments
    } else {
      tournaments.filter { it.type.equals(selectedCategory, ignoreCase = true) }
    }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(DarkBackground)
      .testTag("home_screen_content"),
    contentPadding = PaddingValues(bottom = 90.dp)
  ) {
    // Hero Banner
    item {
      HeroBannerCard()
    }

    // Category Selector
    item {
      LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(categories) { cat ->
          val isSelected = cat == selectedCategory
          FilterChip(
            selected = isSelected,
            onClick = { selectedCategory = cat },
            label = {
              Text(
                text = cat,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 13.sp
              )
            },
            colors = FilterChipDefaults.filterChipColors(
              containerColor = DarkSurfaceElevated,
              labelColor = TextSecondary,
              selectedContainerColor = FireOrange,
              selectedLabelColor = Color.White
            ),
            border = FilterChipDefaults.filterChipBorder(
              borderColor = if (isSelected) FireOrange else CardBorder,
              selectedBorderColor = FireOrange,
              enabled = true,
              selected = isSelected
            ),
            modifier = Modifier.testTag("chip_${cat.lowercase().replace(" ", "_")}")
          )
        }
      }
    }

    // Header Title
    item {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.SportsEsports,
            contentDescription = null,
            tint = FireOrange,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Active Free Fire Tournaments",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.White
          )
        }

        Text(
          text = "${filteredTournaments.size} available",
          fontSize = 12.sp,
          color = TextSecondary
        )
      }
    }

    // Tournaments List
    if (filteredTournaments.isEmpty()) {
      item {
        Card(
          colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              imageVector = Icons.Default.HourglassEmpty,
              contentDescription = null,
              tint = TextMuted,
              modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "No $selectedCategory tournaments found",
              color = TextSecondary,
              fontWeight = FontWeight.SemiBold,
              fontSize = 14.sp
            )
            Text(
              text = "New Free Fire scrims are scheduled regularly.",
              color = TextMuted,
              fontSize = 12.sp
            )
          }
        }
      }
    } else {
      items(filteredTournaments, key = { it.id }) { tournament ->
        val isApplied = appliedTournamentIds.contains(tournament.id)
        TournamentItemCard(
          tournament = tournament,
          isApplied = isApplied,
          onClick = { onTournamentClick(tournament.id) }
        )
      }
    }
  }
}

@Composable
fun HeroBannerCard() {
  Card(
    colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
    shape = RoundedCornerShape(16.dp),
    border = BorderStroke(1.dp, CardBorderHighlight),
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 12.dp)
  ) {
    Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
      Image(
        painter = painterResource(id = R.drawable.ff_hero_banner),
        contentDescription = "Free Fire Tournaments Hero",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
      )

      // Dark fire gradient overlay
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            Brush.verticalGradient(
              colors = listOf(
                Color(0x550B0E14),
                Color(0xEE0B0E14)
              )
            )
          )
      )

      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        Surface(
          color = FireRed,
          shape = RoundedCornerShape(6.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.LocalFireDepartment,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "OFFICIAL FREE FIRE SCRIMS",
              fontSize = 10.sp,
              fontWeight = FontWeight.Black,
              color = Color.White,
              letterSpacing = 1.sp
            )
          }
        }

        Column {
          Text(
            text = "PLAY • COMPETE • WIN",
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            letterSpacing = 0.5.sp
          )
          Text(
            text = "100% Free Entry • Solo, Squad & Custom Scrims Daily",
            fontSize = 12.sp,
            color = FlameAmber,
            fontWeight = FontWeight.Medium
          )
        }
      }
    }
  }
}

@Composable
fun TournamentItemCard(
  tournament: TournamentEntity,
  isApplied: Boolean,
  onClick: () -> Unit
) {
  EsportsCard(
    modifier = Modifier
      .padding(horizontal = 16.dp, vertical = 6.dp)
      .clickable(onClick = onClick)
      .testTag("tournament_card_${tournament.id}")
  ) {
    // Header tags
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Surface(
          color = DarkSurfaceElevated,
          shape = RoundedCornerShape(4.dp),
          border = BorderStroke(1.dp, CardBorder)
        ) {
          Text(
            text = tournament.type.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = FlameAmber,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }

        Surface(
          color = Color(0x2210B981),
          shape = RoundedCornerShape(4.dp)
        ) {
          Text(
            text = tournament.mapName,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = SuccessGreen,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }

        Surface(
          color = Color(0x33FF5722),
          shape = RoundedCornerShape(4.dp)
        ) {
          Text(
            text = "FREE ENTRY",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = FireOrange,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      StatusBadge(status = tournament.status)
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Tournament Name
    Text(
      text = tournament.name,
      fontSize = 16.sp,
      fontWeight = FontWeight.Bold,
      color = Color.White,
      maxLines = 2,
      overflow = TextOverflow.Ellipsis
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Date & Time
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.CalendarToday,
          contentDescription = null,
          tint = TextSecondary,
          modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = tournament.dateStr,
          fontSize = 12.sp,
          color = TextSecondary,
          fontWeight = FontWeight.Medium
        )
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Schedule,
          contentDescription = null,
          tint = TextSecondary,
          modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = tournament.timeStr,
          fontSize = 12.sp,
          color = TextSecondary,
          fontWeight = FontWeight.Medium
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Capacity progress
    val progress = (tournament.currentPlayers.toFloat() / tournament.maxPlayers.toFloat()).coerceIn(0f, 1f)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Players Registered",
        fontSize = 11.sp,
        color = TextMuted
      )
      Text(
        text = "${tournament.currentPlayers}/${tournament.maxPlayers}",
        fontSize = 12.sp,
        color = if (progress >= 1f) ErrorRed else FlameAmber,
        fontWeight = FontWeight.Bold
      )
    }

    Spacer(modifier = Modifier.height(4.dp))
    LinearProgressIndicator(
      progress = { progress },
      modifier = Modifier
        .fillMaxWidth()
        .height(6.dp)
        .clip(RoundedCornerShape(3.dp)),
      color = if (progress >= 1f) ErrorRed else FireOrange,
      trackColor = DarkSurfaceElevated
    )

    Spacer(modifier = Modifier.height(10.dp))

    // Reward preview
    if (tournament.rewardInfo.isNotEmpty()) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .fillMaxWidth()
          .background(DarkSurfaceElevated, RoundedCornerShape(8.dp))
          .padding(8.dp)
      ) {
        Icon(
          imageVector = Icons.Default.EmojiEvents,
          contentDescription = null,
          tint = ElectricYellow,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = tournament.rewardInfo,
          fontSize = 11.sp,
          color = TextSecondary,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
      Spacer(modifier = Modifier.height(12.dp))
    }

    // Action button
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.End
    ) {
      if (isApplied) {
        OutlinedButton(
          onClick = onClick,
          border = BorderStroke(1.dp, SuccessGreen),
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = SuccessGreen)
        ) {
          Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Already Applied • View", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
      } else {
        Button(
          onClick = onClick,
          colors = ButtonDefaults.buttonColors(containerColor = FireOrange),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.testTag("view_tournament_${tournament.id}")
        ) {
          Text("View Tournament", fontWeight = FontWeight.Bold, fontSize = 12.sp)
          Spacer(modifier = Modifier.width(4.dp))
          Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(16.dp))
        }
      }
    }
  }
}
