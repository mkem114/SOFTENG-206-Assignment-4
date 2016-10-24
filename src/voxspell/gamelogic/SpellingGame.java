package voxspell.gamelogic;

import voxspell.gui.App;
import voxspell.inputoutput.WordListReader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <h1>SpellingGame</h1> Represents a spelling game in it's entirety; the level,
 * the words to be quizzed, all the quizzes and the history
 * <p>
 * Implements serializable so that it can be so that the entire game state can
 * be reloaded on next play if wished
 * 
 * @version 1.0
 * @author mkem114 (primary)
 * @author tkro003 (secondary)
 * @since 2016-09-18
 */
public class SpellingGame implements Serializable {
	/**
	 * The serialising ID generated by eclipse
	 */
	private static final long serialVersionUID = 6852462088113077931L;
	private String _startingLevel = "1";
	private SpellingLevel _currentLevel;
	private List<SpellingLevel> _levels;
	private SpellingLevel _customCurrentLevel;
	private List<SpellingLevel> _customLevels;

	/**
	 * Starts a new spelling game based on a filename for a file contains the
	 * words to be loaded
	 */
	public SpellingGame() {
		_levels = new ArrayList<>();
		_customLevels = new ArrayList<>();
		try {
			new WordListReader().addObserver(this).readWords();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Starts a new quiz for the player
	 * 
	 * @return Quiz being played
	 */
	public SpellingQuiz newQuiz() {
		if (_currentLevel == null) {
			startingLevel(_startingLevel);
		}

		return _currentLevel.newQuiz();
	}

	public SpellingQuiz customQuiz() { //TODO
		return _customCurrentLevel.customQuiz();
	}

	public void startingLevel(String level) {
		_startingLevel = level;
		for (SpellingLevel foundLevel : _levels) {
			if (foundLevel.name().equals(level)) {
				_currentLevel = foundLevel;
			}
		}
	}

	public void customLevel(String level) {
		List<SpellingLevel> allLevel = new ArrayList<>(_customLevels);
		allLevel.addAll(_levels);
		for (SpellingLevel foundLevel : allLevel) {
			if (foundLevel.name().equals(level)) {
				_customCurrentLevel = foundLevel;
			}
		}
	}

	/**
	 * Generates statistics for all words on all levels in the structure
	 * [level[word[word, mastered, faulted, failed]]]
	 * 
	 * @return Generated statistics
	 */
	public List<SpellingLevel> statistics() {
		List<SpellingLevel> allLevels = new ArrayList<>(_levels);
		allLevels.addAll(_customLevels);
		return allLevels;
		// TODO figure out how to get to List<<<<
	}

	/**
	 * Add new words to the game using the indexes as relative level numbers
	 *
	 * @param levelWords
	 *            The words to add as [level[words[word]]]
	 */
	public void updateWords(List<List<String>> levelWords, List<String> levelNames) {
		for (int i = 0; i < levelNames.size(); i++) {
			SpellingLevel level = new SpellingLevel(levelNames.get(i), this);
			if (levels().contains(levelNames.get(i))) {
				for (SpellingLevel levelS : _levels) {
					if (level.name().equals(levelNames.get(i))) {
						level = levelS;
					}
				}
			} else if (customLevels().contains(levelNames.get(i))) {
				for (SpellingLevel levelS : _customLevels) {
					if (level.name().equals(levelNames.get(i))) {
						level = levelS;
					}
				}
			}
			for (String word : levelWords.get(i)) {
				level.addWord(word);
			}
			try {
				Integer.parseInt(levelNames.get(i));
				_levels.add(level);
			} catch (Exception e) {
				//TODO
				_customLevels.add(level);
			}
		}
	}

	/**
	 * Promotes the user to the next level in the game
	 */
	protected void levelUp() {
		if (_currentLevel != null) {
			int currentIndex = _levels.indexOf(_currentLevel);
			if (_currentLevel.experience() == 1.0 && currentIndex < _levels.size() - 1) {
				_currentLevel = _levels.get(++currentIndex);
			}
		}
	}

	public List<String> levels() {
		List<String> names = new ArrayList<>();
		for (SpellingLevel level : _levels) {
			names.add(level.name());
		}
		return names;
	}

	public List<String> customLevels() {
		List<String> names = new ArrayList<>();
		for (SpellingLevel level : _customLevels) {
			names.add(level.name());
		}
		return names;
	}

	protected void save() {
		App.inst().saveGame();
	}

	public void previewLevel(String level) {
		ArrayList<SpellingLevel> allLevels = new ArrayList<>(_levels);
		allLevels.addAll(_customLevels);
		for (SpellingLevel search : allLevels) {
			if (search.name().equals(level)) {
				search.preview();
			}
		}
	}
}
