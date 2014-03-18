import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author code4crafter@gmail.com
 */
public class ManyRegex<T> {

	private Map<Pattern, T> patternMap = new LinkedHashMap<Pattern, T>();

	private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

	public ManyRegex<T> put(String regex, T attachment) {
		try {
			readWriteLock.writeLock().lock();
			patternMap.put(Pattern.compile(regex), attachment);
			return this;
		} finally {
			readWriteLock.writeLock().unlock();
		}

	}

	public ManyRegex<T> clear() {
		try {
			readWriteLock.writeLock().lock();
			patternMap.clear();
			return this;
		} finally {
			readWriteLock.writeLock().unlock();
		}
	}

	public T match(String text) {
		try {
			readWriteLock.readLock().lock();
			for (Map.Entry<Pattern, T> patternTEntry : patternMap.entrySet()) {
				Matcher matcher = patternTEntry.getKey().matcher(text);
				if (matcher.find()) {
					return patternTEntry.getValue();
				}
			}
		} finally {
			readWriteLock.readLock().unlock();
		}
		return null;
	}

	public static void main(String[] args) {
		ManyRegex<String> manyRegex = new ManyRegex<String>();
		manyRegex.put("\\d+", "number");
		manyRegex.put("\\w+", "word");
		String text = "asdsd";
		System.out.println(String.format("%s matches %s", text, manyRegex.match(text)));
		text = "12213";
		System.out.println(String.format("%s matches %s", text, manyRegex.match(text)));
        manyRegex.clear();
        manyRegex.put("\\d+", "number");
        text = "asdsd";
        System.out.println(String.format("%s matches %s", text, manyRegex.match(text)));
	}

}
