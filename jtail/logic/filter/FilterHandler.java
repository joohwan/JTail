package jtail.logic.filter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jtail.config.Config;
import jtail.log.MyLogger;
import jtail.logic.FileBlock;
import jtail.logic.FileBlockHandler;
import jtail.logic.Range;
import jtail.logic.ViewContent;
import jtail.ui.tab.TailTab;
import jtail.util.LoggingUtil;
import jtail.util.Value;

/**
 * This class interacts with properties file for filter definition.
 * 
 * @author joohwan oh, 2009-04-27
 * 
 */
public class FilterHandler {
	public static SortedSet<Filter> getAllSavedFilterSet() {
		SortedSet<Filter> filterSet = Config.bean.getFilterSet();
		if (Value.isEmpty(filterSet)) {
			filterSet = new TreeSet<Filter>();
		}
		return filterSet;
	}

	public static void saveFilterSet(SortedSet<Filter> filterSet) {
		Config.bean.setFilterSet(filterSet);
		Config.saveBean();
	}
	
	public static ViewContent getFilteredContentTill(TailTab tailTab, int contentEndIndexWithinFile) {
		FileBlockHandler fileBlockHandler = new FileBlockHandler(tailTab.getFile());
		FileBlock fileBlock = fileBlockHandler.getFileBlockTill(contentEndIndexWithinFile);
		return getFilteredContentTill(tailTab, fileBlock);		
	}

	public static ViewContent getFilteredContentTill(TailTab tailTab, FileBlock lastBlock) {
		long startTimeInMillis = System.currentTimeMillis();
		List<Filter> filterList = tailTab.getFilterList();
		ViewContent viewContent = new ViewContent(lastBlock.getText(), lastBlock.getRange(), null);
		if (Value.isEmpty(filterList)) {
			return viewContent;
		}

		FileBlockHandler fileBlockHandler = new FileBlockHandler(lastBlock.getFile());
		StringBuffer filteredContent = new StringBuffer();
		FileBlock block = lastBlock;
		String text = block.getText();
		int startIndex = block.getStartIndex();
		int endIndex = block.getEndIndex();
		int numberOfBlockRead = 0;
		do {
			numberOfBlockRead++;
			for (Filter filter : filterList) {
				if (filter == null) {
					continue;
				}

				text = applyFilter(text, filter);
			}

			filteredContent.insert(0, text);
			
			if (numberOfBlockRead == getMaximumNumberOfBlocksToBeRead()){
				break;
			}
			if (!fileBlockHandler.previousBlockExists(block)) {
				break;
			}
			
			block = fileBlockHandler.getPreviousFileBlock(block);			
			text = block.getText();
			startIndex = block.getStartIndex();
		} while (filteredContent.length() < FileBlockHandler.getBlockSize());
		
		viewContent.setText(filteredContent.toString());
		viewContent.setRangeWithinFile(new Range(startIndex, endIndex - startIndex));
		MyLogger.debug("getFilteredContentTill took "+LoggingUtil.getElapsedTimeInSeconds(startTimeInMillis, 2));
		MyLogger.debug("it read "+numberOfBlockRead+" blocks ("+(numberOfBlockRead * FileBlockHandler.getBlockSize() / 1024)+" KBytes)");
		return viewContent;
	}
	
	private static int getMaximumNumberOfBlocksToBeRead() {
		return Config.bean.getMaximumNumberOfBlocksToBeRead();
	}

	public static ViewContent getFilteredContentFrom(TailTab tailTab, FileBlock firstBlock) {
		List<Filter> filterList = tailTab.getFilterList();
		ViewContent viewContent = new ViewContent(firstBlock.getText(), null, null);
		if (Value.isEmpty(filterList)) {
			return viewContent;
		}

		FileBlockHandler fileBlockHandler = new FileBlockHandler(firstBlock.getFile());
		StringBuffer filteredContent = new StringBuffer();
		FileBlock block = firstBlock;
		String text = block.getText();
		int startIndex = block.getStartIndex();
		int endIndex = block.getEndIndex();
		int numberOfBlockRead = 0;
		
		do {
			numberOfBlockRead++;
			for (Filter filter : filterList) {
				if (filter == null) {
					continue;
				}

				text = applyFilter(text, filter);
			}

			filteredContent.append(text);
			
			if (numberOfBlockRead == getMaximumNumberOfBlocksToBeRead()){
				break;
			}
			if (!fileBlockHandler.nextBlockExists(block)) {
				break;
			}
			
			block = fileBlockHandler.getNextFileBlock(block);			
			text = block.getText();
			endIndex = block.getEndIndex();
		} while (filteredContent.length() < FileBlockHandler.getBlockSize());
		
		viewContent.setText(filteredContent.toString());
		viewContent.setRangeWithinFile(new Range(startIndex, endIndex - startIndex));
		return viewContent;
	}
		
	public static String applyFilter(String source, Filter filter) {
		long startTimeInMillis = System.currentTimeMillis();
		int matchStart = 0;
		int matchEnd = 0;
		int appendStart = 0;
		int appendEnd = 0;
		Pattern p = Pattern.compile(filter.getRegExp(), filter.getPatternCompileFlag());
		Matcher m = p.matcher(source);
		StringBuffer selected = new StringBuffer();
		String displayString = "";
		while (m.find()) {
			matchStart = m.start();
			if (filter.getFilterType() == FilterType.INCLUDE) {
				appendStart = matchStart;
			} else {
				appendStart = matchEnd;
			}
			matchEnd = m.end();
			if (filter.getFilterType() == FilterType.INCLUDE) {
				appendEnd = matchEnd;
			} else {
				appendEnd = matchStart;
			}
			// displayString = source.substring(appendStart, appendEnd) + Value.NL;
			displayString = source.substring(appendStart, appendEnd);
			selected.append(displayString);
		}

		// append part after last match.
		if (filter.getFilterType() == FilterType.EXCLUDE) {
			displayString = source.substring(matchEnd);
			selected.append(displayString);
		}
		MyLogger.debug("FilterHandler.applyFilter took "+LoggingUtil.getElapsedTimeInSeconds(startTimeInMillis, 2));		
		return selected.toString();
	}

	public static Filter getFilterByFilterName(String filterName) {
		Set<Filter> filterSet = FilterHandler.getAllSavedFilterSet();
		for (Filter filter : filterSet) {
			if (filter != null) {
				if (filter.getFilterName().equals(filterName)) {
					return filter;
				}
			}
		}

		return null;
	}

	public static List<Filter> getSavedFilterList(File file) {
		Map<File, List<String>> map = Config.bean.getUsedFilterListMap();
		List<String> usedFilterNameList = map.get(file);
		List<Filter> usedFilterList = new ArrayList<Filter>();
		if (usedFilterNameList != null) {
			for (String filterName : usedFilterNameList) {
				usedFilterList.add(FilterHandler.getFilterByFilterName(filterName));
			}
		}
		return usedFilterList;
	}

	public static List<Filter> getAvailableFilterList(List<Filter> usedFilterList) {
		SortedSet<Filter> allSavedFilterSet = FilterHandler.getAllSavedFilterSet();
		List<Filter> availableFilterList = new ArrayList<Filter>();
		for (Filter filter : allSavedFilterSet) {
			if (!usedFilterList.contains(filter)) {
				availableFilterList.add(filter);
			}
		}
		return availableFilterList;
	}

	public static void saveFiltersForFile(String[] filterNames, File file) {
		List<String> willBeSavedFilterNameList = Arrays.asList(filterNames);
		Map<File, List<String>> map = Config.bean.getUsedFilterListMap();
		map.put(file, willBeSavedFilterNameList);
		Config.bean.setUsedFilterListMap(map);
	}

	public static List<Filter> getFilterList(String[] filterNames) {
		List<Filter> filterList = new ArrayList<Filter>();
		for (String filterName : filterNames) {
			Filter filter = FilterHandler.getFilterByFilterName(filterName);
			if (filter != null) {
				filterList.add(filter);
			}
		}
		return filterList;
	}
}
