package net.vtst.ow.eclipse.js.closure.builder;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;

import net.vtst.eclipse.easy.ui.properties.stores.IStore;
import net.vtst.eclipse.easy.ui.properties.stores.PluginPreferenceStore;
import net.vtst.ow.closure.compiler.deps.JSLibrary;
import net.vtst.ow.eclipse.js.closure.OwJsClosurePlugin;
import net.vtst.ow.eclipse.js.closure.compiler.IJSLibraryProvider;
import net.vtst.ow.eclipse.js.closure.preferences.ClosurePreferenceRecord;

import org.eclipse.core.runtime.CoreException;

import com.google.javascript.jscomp.AbstractCompiler;

/**
 * Registry of JavaScript libraries, which cache created libraries.  The cached libraries
 * are kept with references, so that they can be garbage collected if no more project uses
 * them.
 * <br>
 * <b>Thread safety:</b> This class is thread safe, as it uses a concurrent hash map.
 * @author Vincent Simonet
 */
public class JSLibraryProviderWithCache implements IJSLibraryProvider {
  
  /**
   * The key identifying a library in the cache.
   */
  private static class LibraryKey {
    private File libraryPath;
    private File pathOfClosureBase;
    LibraryKey(File libraryPath, File pathOfClosureBase) {
      this.libraryPath = libraryPath;
      this.pathOfClosureBase = pathOfClosureBase;
    }
    public boolean equals(Object obj) {
      if (obj instanceof LibraryKey) {
        LibraryKey key = (LibraryKey) obj;
        return (
            key.libraryPath.equals(libraryPath) && 
            key.pathOfClosureBase.equals(pathOfClosureBase));
      }
      return false;
    }
    public int hashCode() {
      int h1 = libraryPath.hashCode();
      int h2 = pathOfClosureBase.hashCode();
      return h1 * h2 + h1 + h2;
    }
  }
  
  private ConcurrentHashMap<LibraryKey, WeakReference<JSLibrary>> cache = 
      new ConcurrentHashMap<LibraryKey, WeakReference<JSLibrary>>();

  private JSLibrary get(LibraryKey key) {
    WeakReference<JSLibrary> ref = cache.get(key);
    if (ref == null) return null;
    else return ref.get();
  }
  
  /**
   * Get a library from the cache, or create it and add it to the cache if it is not in
   * the cache.
   * @param compiler
   * @param libraryPath
   * @param pathOfClosureBase
   * @param isClosureBase
   * @return
   */
  public JSLibrary get(AbstractCompiler compiler, File libraryPath, File pathOfClosureBase) {
    LibraryKey key = new LibraryKey(libraryPath, pathOfClosureBase);
    JSLibrary library = get(key);
    if (library == null) {
      library = new JSLibrary(libraryPath, pathOfClosureBase, getCacheSettings());
      library.setUnits(compiler);
      cache.put(key, new WeakReference<JSLibrary>(library));
    }
    return library;
  }
  
  private JSLibrary.CacheSettings getCacheSettings() {
    ClosurePreferenceRecord r = ClosurePreferenceRecord.getInstance();
    JSLibrary.CacheSettings result = new JSLibrary.CacheSettings();
    IStore prefs = new PluginPreferenceStore(OwJsClosurePlugin.getDefault().getPreferenceStore());
    try {
      result.cacheDepsFiles = r.cacheLibraryDepsFiles.get(prefs);
    } catch (CoreException e) {
      result.cacheDepsFiles = r.cacheLibraryDepsFiles.getDefault();
    }
    try {
      result.cacheStrippedFiles = r.cacheLibraryStrippedFiles.get(prefs);
    } catch (CoreException e) {
      result.cacheStrippedFiles = r.cacheLibraryStrippedFiles.getDefault();
    }
    return result;
  }
  
  public void clear() {
    cache.clear();
  }

}