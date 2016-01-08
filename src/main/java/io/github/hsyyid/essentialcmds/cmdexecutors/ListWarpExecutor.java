/*
 * This file is part of EssentialCmds, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2015 - 2015 HassanS6000
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.github.hsyyid.essentialcmds.cmdexecutors;

import io.github.hsyyid.essentialcmds.internal.AsyncCommandExecutorBase;
import io.github.hsyyid.essentialcmds.utils.PaginatedList;
import io.github.hsyyid.essentialcmds.utils.Utils;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.source.CommandBlockSource;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Optional;

public class ListWarpExecutor extends AsyncCommandExecutorBase
{
	@Override
	public void executeAsync(CommandSource src, CommandContext ctx)
	{
		if (src instanceof Player)
		{
			Player player = (Player) src;
			ArrayList<String> warps = Utils.getWarps();
			
			if (warps.size() == 0)
			{
				player.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "No warps set!"));
				return;
			}

			Optional<Integer> arguments = ctx.<Integer> getOne("page no");
			int pgNo = arguments.orElse(1);

			if (warps.size() > 0)
			{
				PaginatedList pList = new PaginatedList("/warps");
				for (String name : warps)
				{
					Text item = Text.builder(name)
						.onClick(TextActions.runCommand("/warp " + name))
						.onHover(TextActions.showText(Text.of(TextColors.WHITE, "Warp to ", TextColors.GOLD, name)))
						.color(TextColors.DARK_AQUA)
						.style(TextStyles.UNDERLINE)
						.build();

					pList.add(item);
				}
				pList.setItemsPerPage(10);
				// Header
				Text.Builder header = Text.builder();
				header.append(Text.of(TextColors.GREEN, "------------"));
				header.append(Text.of(TextColors.GREEN, " Showing Warps page " + pgNo + " of " + pList.getTotalPages() + " "));
				header.append(Text.of(TextColors.GREEN, "------------"));

				pList.setHeader(header.build());
				
				src.sendMessage(pList.getPage(pgNo));
			}
			else
			{
				src.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "No warps set!"));
			}
		}
		else if (src instanceof ConsoleSource)
		{
			src.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "Must be an in-game player to use /warps!"));
		}
		else if (src instanceof CommandBlockSource)
		{
			src.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "Must be an in-game player to use /warps!"));
		}
	}

	@Nonnull
	@Override
	public String[] getAliases() {
		return new String[] { "warps" };
	}

	@Nonnull
	@Override
	public CommandSpec getSpec() {
		return CommandSpec.builder().description(Text.of("List Warps Command")).permission("essentialcmds.warps.list")
				.arguments(GenericArguments.optional(GenericArguments.onlyOne(GenericArguments.integer(Text.of("page no")))))
				.executor(this).build();
	}
}
