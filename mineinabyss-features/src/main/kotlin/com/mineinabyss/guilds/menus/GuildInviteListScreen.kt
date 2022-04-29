package com.mineinabyss.guilds.menus

import androidx.compose.runtime.Composable
import com.mineinabyss.guilds.database.GuildJoinQueue
import com.mineinabyss.guilds.database.GuildJoinType
import com.mineinabyss.guilds.extensions.getGuildMemberCount
import com.mineinabyss.guilds.extensions.getGuildName
import com.mineinabyss.guilds.extensions.getGuildOwnerFromInvite
import com.mineinabyss.guilds.extensions.removeGuildQueueEntries
import com.mineinabyss.guiy.components.Grid
import com.mineinabyss.guiy.components.Item
import com.mineinabyss.guiy.modifiers.Modifier
import com.mineinabyss.guiy.modifiers.at
import com.mineinabyss.guiy.modifiers.size
import com.mineinabyss.helpers.Text
import com.mineinabyss.helpers.head
import com.mineinabyss.helpers.ui.composables.Button
import com.mineinabyss.idofront.entities.toPlayer
import com.mineinabyss.mineinabyss.core.AbyssContext
import org.bukkit.ChatColor.*
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

@Composable
fun GuildUIScope.GuildInviteListScreen() {
    GuildInvites(Modifier.at(1, 1))
    DenyAllInvites(Modifier.at(8, 4))
    BackButton(Modifier.at(2, 4))
}

@Composable
fun GuildUIScope.GuildInvites(modifier: Modifier = Modifier) {
    /* Transaction to query GuildInvites and playerUUID */
    val owner = player.getGuildOwnerFromInvite().toPlayer()!!
    val memberCount = owner.getGuildMemberCount()
    val invites = transaction(AbyssContext.db) {
        GuildJoinQueue.select {
            (GuildJoinQueue.joinType eq GuildJoinType.Invite) and
                    (GuildJoinQueue.playerUUID eq player.uniqueId)
        }.map { row -> Pair(memberCount, row[GuildJoinQueue.guildId]) }

    }
    Grid(modifier.size(9, 4)) {
        //TODO instead of using a Pair, create a private class and name first/second properly
        invites.sortedBy { it.first }.forEach { (memberCount, guild) ->
            Button(onClick = {
                //TODO get guild from guild param above
                nav.open(GuildScreen.Invite(owner))
            }) {
                Item(player.head(
                    "$GOLD${BOLD}Guildname: $YELLOW$ITALIC${owner.getGuildName()}",
                    "${BLUE}Click this to accept or deny invite.",
                    "${BLUE}Info about the guild can also be found in here.",
                    isFlat = true
                ))
            }
        }
    }
}

@Composable
fun GuildUIScope.DenyAllInvites(modifier: Modifier) = Button(
    onClick = {
        player.removeGuildQueueEntries(GuildJoinType.Invite, true)
        nav.open(GuildScreen.MemberList(guildLevel, player))
        player.sendMessage("$YELLOW${BOLD}❌${YELLOW}You denied all invites!")
    },
    modifier = modifier
) {
    Text("${RED}Decline All Invites")
}
