package com.gmail.trentech.pji.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import com.gmail.trentech.pji.data.InventoryData;

public class CMDDelete implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		InventoryData inventoryData = args.<InventoryData>getOne("inv").get();

		if (inventoryData.getName().equalsIgnoreCase("DEFAULT")) {
			throw new CommandException(Text.of(TextColors.RED, inventoryData.getName(), " inventory be altered or deleted"), false);
		}

		src.sendMessage(Text.builder().color(TextColors.RED).append(Text.of(TextColors.RED, "[WARNING] ", TextColors.YELLOW, "This will delete players inventories and cannot be undone. Confirm? ")).onClick(TextActions.runCommand("/pji:inventory delete yes")).append(Text.of(TextColors.DARK_PURPLE, TextStyles.UNDERLINE, "/inventory delete yes")).build());

		CMDYes.confirm.put(src, inventoryData.getName());

		return CommandResult.success();
	}
}
