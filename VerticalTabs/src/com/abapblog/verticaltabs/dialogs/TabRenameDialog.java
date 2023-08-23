package com.abapblog.verticaltabs.dialogs;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class TabRenameDialog extends TitleAreaDialog {
	private Text txtName;
	private String Name = "";

	public TabRenameDialog(Shell parentShell, String name) {
		super(parentShell);
		this.Name = name;
	}

	@Override
	public void create() {
		super.create();
		setTitle("Rename Tab");
		setMessage("Please enter new Tab Name", IMessageProvider.INFORMATION);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		GridData gd_container = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd_container.heightHint = 40;
		container.setLayoutData(gd_container);
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);

		createName(container);
		return area;
	}

	private void createName(Composite container) {
		Label lbtFirstName = new Label(container, SWT.NONE);
		lbtFirstName.setText("Tab name");

		GridData dataFirstName = new GridData();
		dataFirstName.grabExcessHorizontalSpace = true;
		dataFirstName.horizontalAlignment = GridData.FILL;

		txtName = new Text(container, SWT.BORDER);
		txtName.setLayoutData(dataFirstName);
		txtName.setText(Name);
		txtName.selectAll();
	}

	// save content of the Text fields because they get disposed
	// as soon as the Dialog closes
	private void saveInput() {
		Name = txtName.getText();
	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

	public String getName() {
		return Name;
	}

	public void setName(String Name) {
		this.Name = Name;
		txtName.setText(Name);
	}

}
