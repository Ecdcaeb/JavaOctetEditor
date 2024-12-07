/*
 * Copyright 2022 Enaium
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.enaium.joe.config.extend;

import cn.enaium.joe.config.Config;
import cn.enaium.joe.config.value.EnableValue;
import cn.enaium.joe.config.value.IntegerValue;
import cn.enaium.joe.config.value.ModeValue;
import cn.enaium.joe.service.decompiler.ProcyonDecompiler;
import com.strobel.decompiler.languages.java.BraceEnforcement;
import com.strobel.decompiler.languages.java.BraceStyle;
import com.strobel.decompiler.languages.java.Wrapping;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

/**
 * @author Enaium
 * @since 1.1.0
 */
@SuppressWarnings("unused")
public class ProcyonConfig extends Config {
    public EnableValue IndentNamespaceBody = new EnableValue("IndentNamespaceBody", true, "IndentNamespaceBody");
    public EnableValue IndentClassBody = new EnableValue("IndentClassBody", true, "IndentClassBody");
    public EnableValue IndentInterfaceBody = new EnableValue("IndentInterfaceBody", true, "IndentInterfaceBody");
    public EnableValue IndentEnumBody = new EnableValue("IndentEnumBody", true, "IndentEnumBody");
    public EnableValue IndentMethodBody = new EnableValue("IndentMethodBody", true, "IndentMethodBody");
    public EnableValue IndentBlocks = new EnableValue("IndentBlocks", true, "IndentBlocks");
    public EnableValue IndentSwitchBody = new EnableValue("IndentSwitchBody", false, "IndentSwitchBody");
    public EnableValue IndentCaseBody = new EnableValue("IndentCaseBody", true, "IndentCaseBody");
    public EnableValue IndentBreakStatements = new EnableValue("IndentBreakStatements", true, "IndentBreakStatements");
    public EnableValue AlignEmbeddedUsingStatements = new EnableValue("AlignEmbeddedUsingStatements", true, "AlignEmbeddedUsingStatements");
    public EnableValue AlignEmbeddedIfStatements = new EnableValue("AlignEmbeddedIfStatements", true, "AlignEmbeddedIfStatements");
    public ModeValue<BraceStyle> AnonymousClassBraceStyle = new ModeValue<BraceStyle>("AnonymousClassBraceStyle", BraceStyle.EndOfLine, "AnonymousClassBraceStyle", EnumSet.allOf(BraceStyle.class));
    public ModeValue<BraceStyle> ClassBraceStyle = new ModeValue<BraceStyle>("ClassBraceStyle", BraceStyle.NextLine, "ClassBraceStyle", EnumSet.allOf(BraceStyle.class));
    public ModeValue<BraceStyle> InterfaceBraceStyle = new ModeValue<BraceStyle>("InterfaceBraceStyle", BraceStyle.NextLine, "InterfaceBraceStyle", EnumSet.allOf(BraceStyle.class));
    public ModeValue<BraceStyle> AnnotationBraceStyle = new ModeValue<BraceStyle>("AnnotationBraceStyle", BraceStyle.EndOfLine, "AnnotationBraceStyle", EnumSet.allOf(BraceStyle.class));
    public ModeValue<BraceStyle> EnumBraceStyle = new ModeValue<BraceStyle>("EnumBraceStyle", BraceStyle.NextLine, "EnumBraceStyle", EnumSet.allOf(BraceStyle.class));
    public ModeValue<BraceStyle> ModuleBraceStyle = new ModeValue<BraceStyle>("ModuleBraceStyle", BraceStyle.DoNotChange, "ModuleBraceStyle", EnumSet.allOf(BraceStyle.class));
    public ModeValue<BraceStyle> RecordBraceStyle = new ModeValue<BraceStyle>("RecordBraceStyle", BraceStyle.EndOfLine, "RecordBraceStyle", EnumSet.allOf(BraceStyle.class));
    public ModeValue<BraceStyle> MethodBraceStyle = new ModeValue<BraceStyle>("MethodBraceStyle", BraceStyle.EndOfLine, "MethodBraceStyle", EnumSet.allOf(BraceStyle.class));
    public ModeValue<BraceStyle> InitializerBlockBraceStyle = new ModeValue<BraceStyle>("InitializerBlockBraceStyle", BraceStyle.DoNotChange, "InitializerBlockBraceStyle", EnumSet.allOf(BraceStyle.class));
    public ModeValue<BraceStyle> ConstructorBraceStyle = new ModeValue<BraceStyle>("ConstructorBraceStyle", BraceStyle.EndOfLine, "ConstructorBraceStyle", EnumSet.allOf(BraceStyle.class));
    public ModeValue<BraceStyle> EventBraceStyle = new ModeValue<BraceStyle>("EventBraceStyle", BraceStyle.EndOfLine, "EventBraceStyle", EnumSet.allOf(BraceStyle.class));
    public ModeValue<BraceStyle> EventAddBraceStyle = new ModeValue<BraceStyle>("EventAddBraceStyle", BraceStyle.EndOfLine, "EventAddBraceStyle", EnumSet.allOf(BraceStyle.class));
    public ModeValue<BraceStyle> EventRemoveBraceStyle = new ModeValue<BraceStyle>("EventRemoveBraceStyle", BraceStyle.EndOfLine, "EventRemoveBraceStyle", EnumSet.allOf(BraceStyle.class));
    public ModeValue<BraceStyle> StatementBraceStyle = new ModeValue<BraceStyle>("StatementBraceStyle", BraceStyle.EndOfLine, "StatementBraceStyle", EnumSet.allOf(BraceStyle.class));
    public EnableValue AllowIfBlockInline = new EnableValue("AllowIfBlockInline", false, "AllowIfBlockInline");
    public ModeValue<BraceEnforcement> IfElseBraceEnforcement = new ModeValue<BraceEnforcement>("IfElseBraceEnforcement", BraceEnforcement.DoNotChange, "IfElseBraceEnforcement", EnumSet.allOf(BraceEnforcement.class));
    public ModeValue<BraceEnforcement> ForBraceEnforcement = new ModeValue<BraceEnforcement>("ForBraceEnforcement", BraceEnforcement.DoNotChange, "ForBraceEnforcement", EnumSet.allOf(BraceEnforcement.class));
    public ModeValue<BraceEnforcement> ForEachBraceEnforcement = new ModeValue<BraceEnforcement>("ForEachBraceEnforcement", BraceEnforcement.DoNotChange, "ForEachBraceEnforcement", EnumSet.allOf(BraceEnforcement.class));
    public ModeValue<BraceEnforcement> WhileBraceEnforcement = new ModeValue<BraceEnforcement>("WhileBraceEnforcement", BraceEnforcement.DoNotChange, "WhileBraceEnforcement", EnumSet.allOf(BraceEnforcement.class));
    public ModeValue<BraceEnforcement> UsingBraceEnforcement = new ModeValue<BraceEnforcement>("UsingBraceEnforcement", BraceEnforcement.DoNotChange, "UsingBraceEnforcement", EnumSet.allOf(BraceEnforcement.class));
    public ModeValue<BraceEnforcement> FixedBraceEnforcement = new ModeValue<BraceEnforcement>("FixedBraceEnforcement", BraceEnforcement.DoNotChange, "FixedBraceEnforcement", EnumSet.allOf(BraceEnforcement.class));
    public EnableValue PlaceElseOnNewLine = new EnableValue("PlaceElseOnNewLine", false, "PlaceElseOnNewLine");
    public EnableValue PlaceElseIfOnNewLine = new EnableValue("PlaceElseIfOnNewLine", false, "PlaceElseIfOnNewLine");
    public EnableValue PlaceCatchOnNewLine = new EnableValue("PlaceCatchOnNewLine", false, "PlaceCatchOnNewLine");
    public EnableValue PlaceFinallyOnNewLine = new EnableValue("PlaceFinallyOnNewLine", false, "PlaceFinallyOnNewLine");
    public EnableValue PlaceWhileOnNewLine = new EnableValue("PlaceWhileOnNewLine", false, "PlaceWhileOnNewLine");
    public EnableValue SpaceBeforeMethodDeclarationParentheses = new EnableValue("SpaceBeforeMethodDeclarationParentheses", false, "SpaceBeforeMethodDeclarationParentheses");
    public EnableValue SpaceBetweenEmptyMethodDeclarationParentheses = new EnableValue("SpaceBetweenEmptyMethodDeclarationParentheses", false, "SpaceBetweenEmptyMethodDeclarationParentheses");
    public EnableValue SpaceBeforeMethodDeclarationParameterComma = new EnableValue("SpaceBeforeMethodDeclarationParameterComma", false, "SpaceBeforeMethodDeclarationParameterComma");
    public EnableValue SpaceAfterMethodDeclarationParameterComma = new EnableValue("SpaceAfterMethodDeclarationParameterComma", true, "SpaceAfterMethodDeclarationParameterComma");
    public EnableValue SpaceWithinMethodDeclarationParentheses = new EnableValue("SpaceWithinMethodDeclarationParentheses", false, "SpaceWithinMethodDeclarationParentheses");
    public EnableValue SpaceBeforeMethodCallParentheses = new EnableValue("SpaceBeforeMethodCallParentheses", false, "SpaceBeforeMethodCallParentheses");
    public EnableValue SpaceBetweenEmptyMethodCallParentheses = new EnableValue("SpaceBetweenEmptyMethodCallParentheses", false, "SpaceBetweenEmptyMethodCallParentheses");
    public EnableValue SpaceBeforeMethodCallParameterComma = new EnableValue("SpaceBeforeMethodCallParameterComma", false, "SpaceBeforeMethodCallParameterComma");
    public EnableValue SpaceAfterMethodCallParameterComma = new EnableValue("SpaceAfterMethodCallParameterComma", true, "SpaceAfterMethodCallParameterComma");
    public EnableValue SpaceWithinMethodCallParentheses = new EnableValue("SpaceWithinMethodCallParentheses", false, "SpaceWithinMethodCallParentheses");
    public EnableValue SpaceBeforeFieldDeclarationComma = new EnableValue("SpaceBeforeFieldDeclarationComma", false, "SpaceBeforeFieldDeclarationComma");
    public EnableValue SpaceAfterFieldDeclarationComma = new EnableValue("SpaceAfterFieldDeclarationComma", true, "SpaceAfterFieldDeclarationComma");
    public EnableValue SpaceBeforeLocalVariableDeclarationComma = new EnableValue("SpaceBeforeLocalVariableDeclarationComma", false, "SpaceBeforeLocalVariableDeclarationComma");
    public EnableValue SpaceAfterLocalVariableDeclarationComma = new EnableValue("SpaceAfterLocalVariableDeclarationComma", true, "SpaceAfterLocalVariableDeclarationComma");
    public EnableValue SpaceBeforeConstructorDeclarationParentheses = new EnableValue("SpaceBeforeConstructorDeclarationParentheses", false, "SpaceBeforeConstructorDeclarationParentheses");
    public EnableValue SpaceBetweenEmptyConstructorDeclarationParentheses = new EnableValue("SpaceBetweenEmptyConstructorDeclarationParentheses", false, "SpaceBetweenEmptyConstructorDeclarationParentheses");
    public EnableValue SpaceBeforeConstructorDeclarationParameterComma = new EnableValue("SpaceBeforeConstructorDeclarationParameterComma", false, "SpaceBeforeConstructorDeclarationParameterComma");
    public EnableValue SpaceAfterConstructorDeclarationParameterComma = new EnableValue("SpaceAfterConstructorDeclarationParameterComma", true, "SpaceAfterConstructorDeclarationParameterComma");
    public EnableValue SpaceWithinConstructorDeclarationParentheses = new EnableValue("SpaceWithinConstructorDeclarationParentheses", false, "SpaceWithinConstructorDeclarationParentheses");
    public EnableValue SpaceWithinRecordDeclarationParentheses = new EnableValue("SpaceWithinRecordDeclarationParentheses", false, "SpaceWithinRecordDeclarationParentheses");
    public EnableValue SpaceWithinEnumDeclarationParentheses = new EnableValue("SpaceWithinEnumDeclarationParentheses", false, "SpaceWithinEnumDeclarationParentheses");
    public EnableValue SpaceBeforeIndexerDeclarationBracket = new EnableValue("SpaceBeforeIndexerDeclarationBracket", true, "SpaceBeforeIndexerDeclarationBracket");
    public EnableValue SpaceWithinIndexerDeclarationBracket = new EnableValue("SpaceWithinIndexerDeclarationBracket", false, "SpaceWithinIndexerDeclarationBracket");
    public EnableValue SpaceBeforeIndexerDeclarationParameterComma = new EnableValue("SpaceBeforeIndexerDeclarationParameterComma", false, "SpaceBeforeIndexerDeclarationParameterComma");
    public EnableValue SpaceAfterIndexerDeclarationParameterComma = new EnableValue("SpaceAfterIndexerDeclarationParameterComma", true, "SpaceAfterIndexerDeclarationParameterComma");
    public EnableValue SpaceBeforeDelegateDeclarationParentheses = new EnableValue("SpaceBeforeDelegateDeclarationParentheses", false, "SpaceBeforeDelegateDeclarationParentheses");
    public EnableValue SpaceBetweenEmptyDelegateDeclarationParentheses = new EnableValue("SpaceBetweenEmptyDelegateDeclarationParentheses", false, "SpaceBetweenEmptyDelegateDeclarationParentheses");
    public EnableValue SpaceBeforeDelegateDeclarationParameterComma = new EnableValue("SpaceBeforeDelegateDeclarationParameterComma", false, "SpaceBeforeDelegateDeclarationParameterComma");
    public EnableValue SpaceAfterDelegateDeclarationParameterComma = new EnableValue("SpaceAfterDelegateDeclarationParameterComma", false, "SpaceAfterDelegateDeclarationParameterComma");
    public EnableValue SpaceWithinDelegateDeclarationParentheses = new EnableValue("SpaceWithinDelegateDeclarationParentheses", false, "SpaceWithinDelegateDeclarationParentheses");
    public EnableValue SpaceBeforeNewParentheses = new EnableValue("SpaceBeforeNewParentheses", false, "SpaceBeforeNewParentheses");
    public EnableValue SpaceBeforeIfParentheses = new EnableValue("SpaceBeforeIfParentheses", true, "SpaceBeforeIfParentheses");
    public EnableValue SpaceBeforeWhileParentheses = new EnableValue("SpaceBeforeWhileParentheses", true, "SpaceBeforeWhileParentheses");
    public EnableValue SpaceBeforeForParentheses = new EnableValue("SpaceBeforeForParentheses", true, "SpaceBeforeForParentheses");
    public EnableValue SpaceBeforeForeachParentheses = new EnableValue("SpaceBeforeForeachParentheses", true, "SpaceBeforeForeachParentheses");
    public EnableValue SpaceBeforeCatchParentheses = new EnableValue("SpaceBeforeCatchParentheses", true, "SpaceBeforeCatchParentheses");
    public EnableValue SpaceBeforeSwitchParentheses = new EnableValue("SpaceBeforeSwitchParentheses", true, "SpaceBeforeSwitchParentheses");
    public EnableValue SpaceBeforeSynchronizedParentheses = new EnableValue("SpaceBeforeSynchronizedParentheses", true, "SpaceBeforeSynchronizedParentheses");
    public EnableValue SpaceBeforeUsingParentheses = new EnableValue("SpaceBeforeUsingParentheses", true, "SpaceBeforeUsingParentheses");
    public EnableValue SpaceAroundAssignment = new EnableValue("SpaceAroundAssignment", true, "SpaceAroundAssignment");
    public EnableValue SpaceAroundLogicalOperator = new EnableValue("SpaceAroundLogicalOperator", true, "SpaceAroundLogicalOperator");
    public EnableValue SpaceAroundEqualityOperator = new EnableValue("SpaceAroundEqualityOperator", true, "SpaceAroundEqualityOperator");
    public EnableValue SpaceAroundRelationalOperator = new EnableValue("SpaceAroundRelationalOperator", true, "SpaceAroundRelationalOperator");
    public EnableValue SpaceAroundBitwiseOperator = new EnableValue("SpaceAroundBitwiseOperator", true, "SpaceAroundBitwiseOperator");
    public EnableValue SpaceAroundAdditiveOperator = new EnableValue("SpaceAroundAdditiveOperator", true, "SpaceAroundAdditiveOperator");
    public EnableValue SpaceAroundMultiplicativeOperator = new EnableValue("SpaceAroundMultiplicativeOperator", true, "SpaceAroundMultiplicativeOperator");
    public EnableValue SpaceAroundShiftOperator = new EnableValue("SpaceAroundShiftOperator", true, "SpaceAroundShiftOperator");
    public EnableValue SpaceAroundNullCoalescingOperator = new EnableValue("SpaceAroundNullCoalescingOperator", true, "SpaceAroundNullCoalescingOperator");
    public EnableValue SpacesWithinParentheses = new EnableValue("SpacesWithinParentheses", false, "SpacesWithinParentheses");
    public EnableValue SpacesWithinIfParentheses = new EnableValue("SpacesWithinIfParentheses", false, "SpacesWithinIfParentheses");
    public EnableValue SpacesWithinWhileParentheses = new EnableValue("SpacesWithinWhileParentheses", false, "SpacesWithinWhileParentheses");
    public EnableValue SpacesWithinForParentheses = new EnableValue("SpacesWithinForParentheses", false, "SpacesWithinForParentheses");
    public EnableValue SpacesWithinForeachParentheses = new EnableValue("SpacesWithinForeachParentheses", false, "SpacesWithinForeachParentheses");
    public EnableValue SpacesWithinCatchParentheses = new EnableValue("SpacesWithinCatchParentheses", false, "SpacesWithinCatchParentheses");
    public EnableValue SpacesWithinSwitchParentheses = new EnableValue("SpacesWithinSwitchParentheses", false, "SpacesWithinSwitchParentheses");
    public EnableValue SpacesWithinSynchronizedParentheses = new EnableValue("SpacesWithinSynchronizedParentheses", false, "SpacesWithinSynchronizedParentheses");
    public EnableValue SpacesWithinUsingParentheses = new EnableValue("SpacesWithinUsingParentheses", false, "SpacesWithinUsingParentheses");
    public EnableValue SpacesWithinCastParentheses = new EnableValue("SpacesWithinCastParentheses", false, "SpacesWithinCastParentheses");
    public EnableValue SpacesWithinNewParentheses = new EnableValue("SpacesWithinNewParentheses", false, "SpacesWithinNewParentheses");
    public EnableValue SpacesBetweenEmptyNewParentheses = new EnableValue("SpacesBetweenEmptyNewParentheses", false, "SpacesBetweenEmptyNewParentheses");
    public EnableValue SpaceBeforeNewParameterComma = new EnableValue("SpaceBeforeNewParameterComma", false, "SpaceBeforeNewParameterComma");
    public EnableValue SpaceAfterNewParameterComma = new EnableValue("SpaceAfterNewParameterComma", true, "SpaceAfterNewParameterComma");
    public EnableValue SpaceBeforeConditionalOperatorCondition = new EnableValue("SpaceBeforeConditionalOperatorCondition", true, "SpaceBeforeConditionalOperatorCondition");
    public EnableValue SpaceAfterConditionalOperatorCondition = new EnableValue("SpaceAfterConditionalOperatorCondition", true, "SpaceAfterConditionalOperatorCondition");
    public EnableValue SpaceBeforeConditionalOperatorSeparator = new EnableValue("SpaceBeforeConditionalOperatorSeparator", true, "SpaceBeforeConditionalOperatorSeparator");
    public EnableValue SpaceAfterConditionalOperatorSeparator = new EnableValue("SpaceAfterConditionalOperatorSeparator", true, "SpaceAfterConditionalOperatorSeparator");
    public EnableValue SpacesWithinBrackets = new EnableValue("SpacesWithinBrackets", false, "SpacesWithinBrackets");
    public EnableValue SpacesBeforeBrackets = new EnableValue("SpacesBeforeBrackets", true, "SpacesBeforeBrackets");
    public EnableValue SpaceBeforeBracketComma = new EnableValue("SpaceBeforeBracketComma", false, "SpaceBeforeBracketComma");
    public EnableValue SpaceAfterBracketComma = new EnableValue("SpaceAfterBracketComma", true, "SpaceAfterBracketComma");
    public EnableValue SpaceBeforeForSemicolon = new EnableValue("SpaceBeforeForSemicolon", false, "SpaceBeforeForSemicolon");
    public EnableValue SpaceAfterForSemicolon = new EnableValue("SpaceAfterForSemicolon", true, "SpaceAfterForSemicolon");
    public EnableValue SpaceAfterTypecast = new EnableValue("SpaceAfterTypecast", false, "SpaceAfterTypecast");
    public EnableValue SpaceBeforeArrayDeclarationBrackets = new EnableValue("SpaceBeforeArrayDeclarationBrackets", false, "SpaceBeforeArrayDeclarationBrackets");
    public EnableValue SpaceInNamedArgumentAfterDoubleColon = new EnableValue("SpaceInNamedArgumentAfterDoubleColon", true, "SpaceInNamedArgumentAfterDoubleColon");
    public IntegerValue BlankLinesAfterPackageDeclaration = new IntegerValue("BlankLinesAfterPackageDeclaration", 0, "BlankLinesAfterPackageDeclaration");
    public IntegerValue BlankLinesAfterImports = new IntegerValue("BlankLinesAfterImports", 1, "BlankLinesAfterImports");
    public IntegerValue BlankLinesBeforeFirstDeclaration = new IntegerValue("BlankLinesBeforeFirstDeclaration", 0, "BlankLinesBeforeFirstDeclaration");
    public IntegerValue BlankLinesBetweenTypes = new IntegerValue("BlankLinesBetweenTypes", 1, "BlankLinesBetweenTypes");
    public IntegerValue BlankLinesBetweenFields = new IntegerValue("BlankLinesBetweenFields", 0, "BlankLinesBetweenFields");
    public IntegerValue BlankLinesBetweenEventFields = new IntegerValue("BlankLinesBetweenEventFields", 0, "BlankLinesBetweenEventFields");
    public IntegerValue BlankLinesBetweenMembers = new IntegerValue("BlankLinesBetweenMembers", 1, "BlankLinesBetweenMembers");
    public EnableValue KeepCommentsAtFirstColumn = new EnableValue("KeepCommentsAtFirstColumn", true, "KeepCommentsAtFirstColumn");
    public ModeValue<Wrapping> ArrayInitializerWrapping = new ModeValue<Wrapping>("ArrayInitializerWrapping", Wrapping.WrapIfTooLong, "ArrayInitializerWrapping", EnumSet.allOf(Wrapping.class));
    public ModeValue<BraceStyle> ArrayInitializerBraceStyle = new ModeValue<BraceStyle>("ArrayInitializerBraceStyle", BraceStyle.EndOfLine, "ArrayInitializerBraceStyle", EnumSet.allOf(BraceStyle.class));

    public ProcyonConfig() {
        super("Procyon", Set.of(ProcyonDecompiler.cachedFormattingOptions));
        this.postInit();
    }
}
