package net.vtst.ow.eclipse.js.closure.launching.compiler;

import java.util.List;

import net.vtst.eclipse.easy.ui.launching.EasyLaunchShortcut;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;

public class ClosureCompilerLaunchShortcut extends EasyLaunchShortcut<IFile> {

  @Override
  protected IFile getSelection(Iterable<IResource> selection)
      throws CoreException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected String getLaunchConfigurationTypeID() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected List<ILaunchConfiguration> findLaunchConfigurations(
      IFile selection, String mode) throws CoreException {
    // TODO Auto-generated method stub
    return null;
  }

}