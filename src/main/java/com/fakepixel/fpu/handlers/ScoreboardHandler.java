package com.fakepixel.fpu.handlers;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.StringUtils;

public class ScoreboardHandler {
    public static String cleanSB(String scoreboard) {
        char[] nvString = StringUtils.stripControlCodes(scoreboard).toCharArray();
        StringBuilder cleaned = new StringBuilder();
        for (char c : nvString) {
            if (c > 20 && c < 127) {
                cleaned.append(c);
            }
        }
        return cleaned.toString();
    }

    public static List<String> getSidebarLines() {
        Scoreboard scoreboard;
        ScoreObjective objective;
        Collection<Score> scores;
        List<String> lines = new ArrayList<>();
        if (Minecraft.getMinecraft().theWorld != null && (scoreboard = Minecraft.getMinecraft().theWorld.getScoreboard()) != null && (objective = scoreboard.getObjectiveInDisplaySlot(1)) != null) {
            Collection<Score> scores2 = scoreboard.getSortedScores(objective);
            List<Score> list = (List) scores2.stream().filter(input -> {
                return (input == null || input.getPlayerName() == null || input.getPlayerName().startsWith("#")) ? false : true;
            }).collect(Collectors.toList());
            if (list.size() > 15) {
                scores = Lists.newArrayList(Iterables.skip(list, scores2.size() - 15));
            } else {
                scores = list;
            }
            for (Score score : scores) {
                ScorePlayerTeam team = scoreboard.getPlayersTeam(score.getPlayerName());
                lines.add(ScorePlayerTeam.formatPlayerName(team, score.getPlayerName()));
            }
            return lines;
        }
        return lines;
    }
}
