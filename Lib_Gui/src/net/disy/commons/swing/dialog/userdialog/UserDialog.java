/**
 * Copyright (C) 2005, 2011 disy Informationssysteme GmbH and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */
package net.disy.commons.swing.dialog.userdialog;

import net.disy.commons.swing.dialog.core.AbstractDialog;
import net.disy.commons.swing.dialog.core.DialogResult;
import net.disy.commons.swing.dialog.core.IDialogHelpHandler;
import net.disy.commons.swing.dialog.core.IDialogResult;
import net.disy.commons.swing.dialog.core.IVetoDialogCloseHandler;
import net.disy.commons.swing.dialog.core.internal.DialogButtonBarBuilder;
import net.disy.commons.swing.dialog.core.preferences.IDialogPreferences;
import net.disy.commons.swing.dialog.userdialog.buttons.IDialogButtonConfiguration;
import net.disy.commons.swing.dialog.userdialog.page.IDialogPage;
import net.disy.commons.swing.util.GuiUtilities;
import net.disy.commons.swing.util.RelativePosition;
import net.sf.anathema.lib.gui.action.ActionConfiguration;
import net.sf.anathema.lib.gui.action.IActionConfiguration;
import net.sf.anathema.lib.gui.action.SmartAction;

import javax.swing.JButton;
import javax.swing.JComponent;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserDialog extends AbstractDialog implements IUserDialogContainer {

  private final DialogPageControl dialogControl;
  private JButton okButton;
  private JButton cancelButton;
  private boolean neverVisualized = true;
  private final RelativePosition relativePosition;
  private boolean showErrorOnStartup = false;

  public UserDialog(
      final Component parentComponent,
      final IDialogConfiguration<? extends IDialogPage> userDialog) {
    this(parentComponent, userDialog, RelativePosition.CENTER);
  }

  public UserDialog(
      final Component parentComponent,
      final IDialogConfiguration<?> dialogConfiguration,
      final RelativePosition relativePosition) {
    super(parentComponent, dialogConfiguration);
    dialogControl = new DialogPageControl(dialogConfiguration.getDialogPage());
    dialogConfiguration.setUserDialogContainer(this);
    dialogControl.setDialogControl(this);
    initializeContent();
    setContent(dialogControl.getContent());
    updateAll();
    dialogControl.requestFocus();
    this.relativePosition = relativePosition;
  }

  public UserDialog(final Component parentComponent, final IDialogPage dialogPage) {
    this(parentComponent, new DefaultDialogConfigurationBuilder<IDialogPage>(dialogPage).build());
  }

  protected IDialogConfiguration<?> getConfiguration() {
    return (IDialogConfiguration<?>) getGenericDialog();
  }

  private void updateAll() {
    updateDescription();
    updateTitle();
    updateMessage();
    updateButtons();
  }

  @Override
  public void updateDescription() {
    setDescription(getDialogControl().getDescription());
  }

  @Override
  public void updateTitle() {
    setTitle(getDialogControl().getTitle());
  }

  @Override
  public void updateMessage() {
    setMessage(getDialogControl().getMessage());
  }

  @Override
  public void updateButtons() {
    okButton.setEnabled(getDialogControl().canFinish());
  }

  @Override
  protected final JComponent createButtonBar() {
    final JComponent[] buttons = createButtons();
    if (buttons.length > 0 && buttons[0] instanceof JButton) {
      setDefaultButton((JButton) buttons[0]);
    }
    final DialogButtonBarBuilder buttonBarBuilder = new DialogButtonBarBuilder();
    buttonBarBuilder.addButtons(buttons);
    final IDialogHelpHandler helpHandler = getDialogControl().getHelpHandler();
    if (helpHandler != null) {
      buttonBarBuilder.setHelpHandler(helpHandler);
    }
    final JComponent leftComponent = getConfiguration().createOptionalButtonPanelLeftComponent();
    if (leftComponent != null) {
      buttonBarBuilder.addLeftSideComponent(leftComponent);
    }
    return buttonBarBuilder.createButtonBar();
  }

  private final JComponent[] createButtons() {
    final IDialogButtonConfiguration buttonConfiguration = getConfiguration()
        .getButtonConfiguration();
    final IActionConfiguration okActionConfiguration = buttonConfiguration
        .getOkActionConfiguration();

    final SmartAction okAction = new SmartAction(okActionConfiguration != null
        ? okActionConfiguration
        : new ActionConfiguration()) {
      @Override
      protected void execute(final Component parentComponent) {
        requestFinish();
      }
    };

    okButton = new JButton(okAction);

    final IActionConfiguration cancelActionConfiguration = buttonConfiguration
        .getCancelActionConfiguration();
    final SmartAction cancelAction = new SmartAction(cancelActionConfiguration != null
        ? cancelActionConfiguration
        : new ActionConfiguration()) {
      @Override
      protected void execute(final Component parentComponent) {
        performCancel(parentComponent);
      }
    };
    cancelButton = new JButton(cancelAction);

    final List<JComponent> buttonList = new ArrayList<JComponent>();
    if (okActionConfiguration != null) {
      buttonList.add(okButton);
    }

    final JComponent[] additionalButtons = getConfiguration().createAdditionalButtons();
    buttonList.addAll(Arrays.asList(additionalButtons));
    buttonList.addAll(Arrays.asList(createAdditionalButtons()));

    if (cancelActionConfiguration != null) {
      buttonList.add(cancelButton);
    }
    return buttonList.toArray(new JComponent[buttonList.size()]);
  }

  protected JComponent[] createAdditionalButtons() {
    return new JComponent[0];
  }

  private final boolean okPressed() {
    if (!getDialogControl().performOk()) {
      return false;
    }
    final IVetoDialogCloseHandler closeHandler = getConfiguration().getVetoCloseHandler();
    return closeHandler.handleDialogAboutToClose(new DialogResult(false), getDialog().getWindow());
  }

  public DialogPageControl getDialogControl() {
    return dialogControl;
  }

  @Override
  protected final boolean cancelPressed(final Component parentComponent) {
    if (!getDialogControl().performCancel()) {
      return false;
    }
    final IVetoDialogCloseHandler closeHandler = getConfiguration().getVetoCloseHandler();
    return closeHandler.handleDialogAboutToClose(new DialogResult(true), parentComponent);
  }

  @Override
  public void setVisible(final boolean visible) {
    if (getConfiguration().isVisible()) {
      if (visible) {
        if (neverVisualized) {
          final Dimension customizedPreferedSize = getConfiguration().getCustomizedPreferedSize();
          if (customizedPreferedSize != null) {
            getDialog().getWindow().setSize(customizedPreferedSize);
          }
          placeRelativeToOwner();
          neverVisualized = false;
        }
        final IDialogPage page = getConfiguration().getDialogPage();
        page.enter();
        adjustToPreferences();
        getDialog().show();
      }
      else {
        closeDialog();
      }
    }
  }

  private void placeRelativeToOwner() {
    IDialogPreferences preference = getConfiguration().getPreferences();
    if (preference != null && preference.getBounds() != null) {
      return;
    }
    GuiUtilities.placeRelativeToOwner(getDialog().getWindow(), relativePosition);
  }

  @Override
  public IDialogResult show() {
    checkInputValidIfNeccessary();
    setVisible(true);
    return new DialogResult(isCanceled());
  }

  private void checkInputValidIfNeccessary() {
    if (showErrorOnStartup) {
      getDialogControl().checkInputValid();
    }
  }

  @Override
  public final void requestFinish() {
    GuiUtilities.stopCellEditing(getDialog().getContentPane());
    if (!getDialogControl().canFinish()) {
      return;
    }
    if (okPressed()) {
      closeDialog();
      getCloseHandler().handleDialogClose(new DialogResult(false));
    }
  }

  @Override
  protected void closeDialog() {
    super.closeDialog();
    final IDialogPage page = getConfiguration().getDialogPage();
    page.leave();
    page.dispose();
  }

  @Override
  public void showNonModal() {
    showNonModal(IDialogCloseHandler.NULL_HANDLER);
  }

  @Override
  public void showNonModal(final IDialogCloseHandler dialogCloseHandler) {
    showDialog(dialogCloseHandler, false);
  }

  private void showDialog(final IDialogCloseHandler dialogCloseHandler, boolean modal) {
    checkInputValidIfNeccessary();
    setCloseHandler(dialogCloseHandler);
    getDialog().setModal(modal);
    setVisible(true);
  }
}