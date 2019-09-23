/*
 * Copyright (c) 1997, 2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package java.util;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Hash table based implementation of the <tt>Map</tt> interface.  This
 * implementation provides all of the optional map operations, and permits
 * <tt>null</tt> values and the <tt>null</tt> key.  (The <tt>HashMap</tt>
 * class is roughly equivalent to <tt>Hashtable</tt>, except that it is
 * unsynchronized and permits nulls.)  This class makes no guarantees as to
 * the order of the map; in particular, it does not guarantee that the order
 * will remain constant over time.
 *
 * <p>This implementation provides constant-time performance for the basic
 * operations (<tt>get</tt> and <tt>put</tt>), assuming the hash function
 * disperses the elements properly among the buckets.  Iteration over
 * collection views requires time proportional to the "capacity" of the
 * <tt>HashMap</tt> instance (the number of buckets) plus its size (the number
 * of key-value mappings).  Thus, it's very important not to set the initial
 * capacity too high (or the load factor too low) if iteration performance is
 * important.
 * /// 遍历的时间是O(capacity+size)！
 * <p>An instance of <tt>HashMap</tt> has two parameters that affect its
 * performance: <i>initial capacity</i> and <i>load factor</i>.  The
 * <i>capacity</i> is the number of buckets in the hash table, and the initial
 * capacity is simply the capacity at the time the hash table is created.  The
 * <i>load factor</i> is a measure of how full the hash table is allowed to
 * get before its capacity is automatically increased.  When the number of
 * entries in the hash table exceeds the product of the load factor and the
 * current capacity, the hash table is <i>rehashed</i> (that is, internal data
 * structures are rebuilt) so that the hash table has approximately twice the
 * number of buckets.
 *
 * <p>As a general rule, the default load factor (.75) offers a good
 * tradeoff between time and space costs.  Higher values decrease the
 * space overhead but increase the lookup cost (reflected in most of
 * the operations of the <tt>HashMap</tt> class, including
 * <tt>get</tt> and <tt>put</tt>).  The expected number of entries in
 * the map and its load factor should be taken into account when
 * setting its initial capacity, so as to minimize the number of
 * rehash operations.  If the initial capacity is greater than the
 * maximum number of entries divided by the load factor, no rehash
 * operations will ever occur.
 *
 * <p>If many mappings are to be stored in a <tt>HashMap</tt>
 * instance, creating it with a sufficiently large capacity will allow
 * the mappings to be stored more efficiently than letting it perform
 * automatic rehashing as needed to grow the table.  Note that using
 * many keys with the same {@code hashCode()} is a sure way to slow
 * down performance of any hash table. To ameliorate impact, when keys
 * are {@link Comparable}, this class may use comparison order among
 * keys to help break ties.
 *
 * <p><strong>Note that this implementation is not synchronized.</strong>
 * If multiple threads access a hash map concurrently, and at least one of
 * the threads modifies the map structurally, it <i>must</i> be
 * synchronized externally.  (A structural modification is any operation
 * that adds or deletes one or more mappings; merely changing the value
 * associated with a key that an instance already contains is not a
 * structural modification.)  This is typically accomplished by
 * synchronizing on some object that naturally encapsulates the map.
 *
 * If no such object exists, the map should be "wrapped" using the
 * {@link Collections#synchronizedMap Collections.synchronizedMap}
 * method.  This is best done at creation time, to prevent accidental
 * unsynchronized access to the map:<pre>
 *   Map m = Collections.synchronizedMap(new HashMap(...));</pre>
 *
 * <p>The iterators returned by all of this class's "collection view methods"
 * are <i>fail-fast</i>: if the map is structurally modified at any time after
 * the iterator is created, in any way except through the iterator's own
 * <tt>remove</tt> method, the iterator will throw a
 * {@link ConcurrentModificationException}.  Thus, in the face of concurrent
 * modification, the iterator fails quickly and cleanly, rather than risking
 * arbitrary, non-deterministic behavior at an undetermined time in the
 * future.
 *
 * <p>Note that the fail-fast behavior of an iterator cannot be guaranteed
 * as it is, generally speaking, impossible to make any hard guarantees in the
 * presence of unsynchronized concurrent modification.  Fail-fast iterators
 * throw <tt>ConcurrentModificationException</tt> on a best-effort basis.
 * Therefore, it would be wrong to write a program that depended on this
 * exception for its correctness: <i>the fail-fast behavior of iterators
 * should be used only to detect bugs.</i>
 *
 * <p>This class is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author Doug Lea
 * @author Josh Bloch
 * @author Arthur van Hoff
 * @author Neal Gafter
 * @see Object#hashCode()
 * @see Collection
 * @see Map
 * @see TreeMap
 * @see Hashtable
 * @since 1.2
 */

/**
 * HashMap是常用的Java集合之一，是基于哈希表的Map接口的实现。与HashTable主要区别为不支持同步和允许null作为key和value。
 * HashMap非线程安全，即任一时刻可以有多个线程同时写HashMap，可能会导致数据的不一致。
 * 如果需要满足线程安全，可以用 Collections的synchronizedMap方法使HashMap具有线程安全的能力，或者使用ConcurrentHashMap。
 * 在JDK1.6中，HashMap采用数组+链表实现，即使用链表处理冲突，同一hash值的链表都存储在一个链表里。
 * 但是当位于一个数组中的元素较多，即hash值相等的元素较多时，通过key值依次查找的效率较低。
 * 而JDK1.8中，HashMap采用数组+链表+红黑树实现，当链表长度超过阈值8时，将链表转换为红黑树，这样大大减少了查找时间。
 * 原本Map.Entry接口的实现类Entry改名为了Node。转化为红黑树时改用另一种实现TreeNode。
 *
 * QA:
 * 遍历的时间是O(capacity+size),为什么？
 * hash冲突怎么解决？
 * why the default initial capacity MUST be a power of two?
 * 在什么情况下扩张，是如何扩张的?
 * 元素减少时，会收缩吗?
 */
public class HashMap<K, V> extends AbstractMap<K, V>
    implements Map<K, V>, Cloneable, Serializable {

  private static final long serialVersionUID = 362498820763181265L;

  /*
   * Implementation notes.
   *
   * This map usually acts as a binned (bucketed) hash table, but
   * when bins get too large, they are transformed into bins of
   * TreeNodes, each structured similarly to those in
   * java.util.TreeMap. Most methods try to use normal bins, but
   * relay to TreeNode methods when applicable (simply by checking
   * instanceof a node).  Bins of TreeNodes may be traversed and
   * used like any others, but additionally support faster lookup
   * when overpopulated. However, since the vast majority of bins in
   * normal use are not overpopulated, checking for existence of
   * tree bins may be delayed in the course of table methods.
   *
   * Tree bins (i.e., bins whose elements are all TreeNodes) are
   * ordered primarily by hashCode, but in the case of ties, if two
   * elements are of the same "class C implements Comparable<C>",
   * type then their compareTo method is used for ordering. (We
   * conservatively check generic types via reflection to validate
   * this -- see method comparableClassFor).  The added complexity
   * of tree bins is worthwhile in providing worst-case O(log n)
   * operations when keys either have distinct hashes or are
   * orderable, Thus, performance degrades gracefully under
   * accidental or malicious usages in which hashCode() methods
   * return values that are poorly distributed, as well as those in
   * which many keys share a hashCode, so long as they are also
   * Comparable. (If neither of these apply, we may waste about a
   * factor of two in time and space compared to taking no
   * precautions. But the only known cases stem from poor user
   * programming practices that are already so slow that this makes
   * little difference.)
   *
   * Because TreeNodes are about twice the size of regular nodes, we
   * use them only when bins contain enough nodes to warrant use
   * (see TREEIFY_THRESHOLD). And when they become too small (due to
   * removal or resizing) they are converted back to plain bins.  In
   * usages with well-distributed user hashCodes, tree bins are
   * rarely used.  Ideally, under random hashCodes, the frequency of
   * nodes in bins follows a Poisson distribution
   * (http://en.wikipedia.org/wiki/Poisson_distribution) with a
   * parameter of about 0.5 on average for the default resizing
   * threshold of 0.75, although with a large variance because of
   * resizing granularity. Ignoring variance, the expected
   * occurrences of list size k are (exp(-0.5) * pow(0.5, k) /
   * factorial(k)). The first values are:
   *
   * 0:    0.60653066
   * 1:    0.30326533
   * 2:    0.07581633
   * 3:    0.01263606
   * 4:    0.00157952
   * 5:    0.00015795
   * 6:    0.00001316
   * 7:    0.00000094
   * 8:    0.00000006
   * more: less than 1 in ten million
   *
   * The root of a tree bin is normally its first node.  However,
   * sometimes (currently only upon Iterator.remove), the root might
   * be elsewhere, but can be recovered following parent links
   * (method TreeNode.root()).
   *
   * All applicable internal methods accept a hash code as an
   * argument (as normally supplied from a public method), allowing
   * them to call each other without recomputing user hashCodes.
   * Most internal methods also accept a "tab" argument, that is
   * normally the current table, but may be a new or old one when
   * resizing or converting.
   *
   * When bin lists are treeified, split, or untreeified, we keep
   * them in the same relative access/traversal order (i.e., field
   * Node.next) to better preserve locality, and to slightly
   * simplify handling of splits and traversals that invoke
   * iterator.remove. When using comparators on insertion, to keep a
   * total ordering (or as close as is required here) across
   * rebalancings, we compare classes and identityHashCodes as
   * tie-breakers.
   *
   * The use and transitions among plain vs tree modes is
   * complicated by the existence of subclass LinkedHashMap. See
   * below for hook methods defined to be invoked upon insertion,
   * removal and access that allow LinkedHashMap internals to
   * otherwise remain independent of these mechanics. (This also
   * requires that a map instance be passed to some utility methods
   * that may create new nodes.)
   *
   * The concurrent-programming-like SSA-based coding style helps
   * avoid aliasing errors amid all of the twisty pointer operations.
   */

  /** The default initial capacity - MUST be a power of two. */
  /**
   * 默认的初始容量（容量为HashMap中槽的数目）是16，且实际容量必须是2的整数次幂。
   *
   * 0000 0001 右移4位 0001 0000为16，主干数组的初始容量为16，而且这个数组 //必须是2的倍数(后面说为什么是2的倍数)
   */
  static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16

  /**
   * The maximum capacity, used if a higher value is implicitly specified
   * by either of the constructors with arguments.
   * MUST be a power of two <= 1<<30.
   */
  /**
   * 最大容量（必须是2的幂且小于2的30次方，传入容量过大将被这个值替换）
   *
   * 最大容量为int的最大值除2
   */
  static final int MAXIMUM_CAPACITY = 1 << 30;

  /**
   * The load factor used when none specified in constructor.
   */
  /**
   * 默认装填因子0.75，如果当前键值对个数 >= HashMap最大容量*装填因子，进行rehash操作
   *
   * 空间换时间。 越大， 碰撞的概率越大.  TODO
   *
   * AQS 为什么通过静态内部类写？
   * volate怎么保证可见性？ openjdk看源码，可以看到可重入怎么做的？
   * JOC 源码
   */
  static final float DEFAULT_LOAD_FACTOR = 0.75f;

  /**
   * The bin count threshold for using a tree rather than list for a
   * bin.  Bins are converted to trees when adding an element to a
   * bin with at least this many nodes. The value must be greater
   * than 2 and should be at least 8 to mesh with assumptions in
   * tree removal about conversion back to plain bins upon
   * shrinkage.
   */
  /**
   * JDK1.8 新加，阈值，Entry链表最大长度。
   * 当桶中节点上的链表长度大于8，将链表转成红黑树存储；
   */
  static final int TREEIFY_THRESHOLD = 8;

  /**
   * The bin count threshold for untreeifying a (split) bin during a
   * resize operation. Should be less than TREEIFY_THRESHOLD, and at
   * most 6 to mesh with shrinkage detection under removal.
   */
  /**
   * JDK1.8 新加，hash表扩容后，如果发现某一个红黑树的长度小于6，则会重新退化为链表(将红黑树转为链表存储)
   */
  static final int UNTREEIFY_THRESHOLD = 6;

  /**
   * The smallest table capacity for which bins may be treeified.
   * (Otherwise the table is resized if too many nodes in a bin.)
   * Should be at least 4 * TREEIFY_THRESHOLD to avoid conflicts
   * between resizing and treeification thresholds.
   */
  /**
   * 桶可能被转化为树形结构的最小容量。
   * 当哈希表的大小超过这个阈值，才会把链式结构转化成树型结构，否则仅采取扩容来尝试减少冲突。
   * 也就是说，当hashmap容量大于64时，链表才能转成红黑树。
   *
   * 应该至少4*TREEIFY_THRESHOLD来避免扩容和树形结构化之间的冲突。
   */
  static final int MIN_TREEIFY_CAPACITY = 64;

  /**
   * Basic hash bin node, used for most entries.  (See below for
   * TreeNode subclass, and in LinkedHashMap for its Entry subclass.)
   */
  /**
   * JDK1.6用Entry描述键值对，JDK1.8中用Node代替Entry
   */
  static class Node<K, V> implements Map.Entry<K, V> {

    // hash存储key的hashCode, 用来定位数组索引位置
    final int hash;
    // final:一个键值对的key不可改变
    final K key;
    // 值
    V value;
    //指向下个节点的引用
    Node<K, V> next;

    Node(int hash, K key, V value, Node<K, V> next) {
      this.hash = hash;
      this.key = key;
      this.value = value;
      this.next = next;
    }

    public final K getKey() {
      return key;
    }

    public final V getValue() {
      return value;
    }

    public final String toString() {
      return key + "=" + value;
    }

    public final int hashCode() {
      return Objects.hashCode(key) ^ Objects.hashCode(value);
    }

    public final V setValue(V newValue) {
      V oldValue = value;
      value = newValue;
      return oldValue;
    }

    public final boolean equals(Object o) {
      if (o == this) {
        return true;
      }
      if (o instanceof Map.Entry) {
        Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
        if (Objects.equals(key, e.getKey()) &&
            Objects.equals(value, e.getValue())) {
          return true;
        }
      }
      return false;
    }
  }

    /* ---------------- Static utilities -------------- */

  /**
   * Computes key.hashCode() and spreads (XORs) higher bits of hash
   * to lower.  Because the table uses power-of-two masking, sets of
   * hashes that vary only in bits above the current mask will
   * always collide. (Among known examples are sets of Float keys
   * holding consecutive whole numbers in small tables.)  So we
   * apply a transform that spreads the impact of higher bits
   * downward. There is a tradeoff between speed, utility, and
   * quality of bit-spreading. Because many common sets of hashes
   * are already reasonably distributed (so don't benefit from
   * spreading), and because we use trees to handle large sets of
   * collisions in bins, we just XOR some shifted bits in the
   * cheapest possible way to reduce systematic lossage, as well as
   * to incorporate impact of the highest bits that would otherwise
   * never be used in index calculations because of table bounds.
   */
  /**
   * HashMap中键值对的存储形式为链表节点，hashCode相同的节点（位于同一个桶）用链表组织
   * hash方法分为三步:
   * 1.取key的hashCode
   * 2.key的hashCode高16位异或低16位
   * 3.将第一步和第二步得到的结果进行取模运算。
   */
  static final int hash(Object key) {
    int h;
    // 让高位码产生作用: https://www.zhihu.com/question/20733617
    //计算key的hashCode, h = Objects.hashCode(key)
    //h >>> 16表示对h无符号右移16位，高位补0，然后h与h >>> 16按位异或
    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
  }

  /**
   * Returns x's Class if it is of the form "class C implements
   * Comparable<C>", else null.
   */
  /**
   * 如果参数x实现了Comparable接口，返回参数x的类名，否则返回null
   */
  static Class<?> comparableClassFor(Object x) {
    if (x instanceof Comparable) {
      Class<?> c;
      Type[] ts, as;
      Type t;
      ParameterizedType p;
      if ((c = x.getClass()) == String.class) // bypass checks
      {
        return c;
      }
      if ((ts = c.getGenericInterfaces()) != null) {
        for (int i = 0; i < ts.length; ++i) {
          if (((t = ts[i]) instanceof ParameterizedType) &&
              ((p = (ParameterizedType) t).getRawType() ==
                  Comparable.class) &&
              (as = p.getActualTypeArguments()) != null &&
              as.length == 1 && as[0] == c) // type arg is c
          {
            return c;
          }
        }
      }
    }
    return null;
  }

  /**
   * Returns k.compareTo(x) if x matches kc (k's screened comparable
   * class), else 0.
   */
  /**
   * 如果x的类型为kc，则返回k.compareTo(x)，否则返回0
   */
  @SuppressWarnings({"rawtypes", "unchecked"}) // for cast to Comparable
  static int compareComparables(Class<?> kc, Object k, Object x) {
    return (x == null || x.getClass() != kc ? 0 :
        ((Comparable) k).compareTo(x));
  }

  /** Returns a power of two size for the given target capacity. ///大于指定数的2的指数 */
  /**
   * 结果为>=cap的最小2的自然数幂。
   *
   * tableSizeFor的作用就是，如果传入A，当A大于0，小于定义的最大容量时，如果A是2次幂则返回A，
   * 否则将A转化为一个比A大且差距最小的2次幂。
   *
   * 例如传入7返回8，传入8返回8，传入9返回16
   */
  static final int tableSizeFor(int cap) {
    int n = cap - 1;
    n |= n >>> 1;
    n |= n >>> 2;
    n |= n >>> 4;
    n |= n >>> 8;
    n |= n >>> 16;
    return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
  }

    /* ---------------- Fields -------------- */

  /**
   * The table, initialized on first use, and resized as
   * necessary. When allocated, length is always a power of two.
   * (We also tolerate length zero in some operations to allow
   * bootstrapping mechanics that are currently not needed.)
   */
  /**
   * 哈希桶数组，分配的时候，table的长度总是2的幂
   */
  transient Node<K, V>[] table;

  /**
   * Holds cached entrySet(). Note that AbstractMap fields are used
   * for keySet() and values().
   */
  /**
   * HashMap将数据转换成set的另一种存储形式，这个变量主要用于迭代功能
   */
  transient Set<Map.Entry<K, V>> entrySet;

  /**
   * The number of key-value mappings contained in this map.
   */
  /**
   * 实际存储的数量，则HashMap的size()方法，实际返回的就是这个值，isEmpty()也是判断该值是否为0
   */
  transient int size;

  /**
   * The number of times this HashMap has been structurally modified
   * Structural modifications are those that change the number of mappings in
   * the HashMap or otherwise modify its internal structure (e.g.,
   * rehash).  This field is used to make iterators on Collection-views of
   * the HashMap fail-fast.  (See ConcurrentModificationException).
   */
  /**
   * hashmap结构被改变的次数，fail-fast机制
   */
  transient int modCount;

  /**
   * The next size value at which to resize (capacity * load factor).
   *
   * @serial
   */
  // (The javadoc description is true upon serialization.
  // Additionally, if the table array has not been allocated, this
  // field holds the initial array capacity, or zero signifying
  // DEFAULT_INITIAL_CAPACITY.)
  /**
   * HashMap的扩容阈值，在HashMap中存储的Node键值对超过这个数量时，自动扩容容量为原来的二倍
   *
   * 临界值=主干数组容量*负载因子
   * @serial
   */
  int threshold;

  /**
   * The load factor for the hash table.
   *
   * @serial
   */
  /**
   * HashMap的负加载因子，可计算出当前table长度下的扩容阈值：threshold = loadFactor * table.length
   *
   * @serial
   */
  final float loadFactor;

    /* ---------------- Public operations -------------- */

  /**
   * Constructs an empty <tt>HashMap</tt> with the specified initial
   * capacity and load factor.
   *
   * @param initialCapacity the initial capacity
   * @param loadFactor      the load factor
   * @throws IllegalArgumentException if the initial capacity is negative or the load factor is
   *                                  nonpositive
   */
  /**
   * 使用指定的初始化容量initial capacity 和加载因子load factor构造一个空HashMap
   *
   * @param initialCapacity 初始化容量
   * @param loadFactor      加载因子
   * @throws IllegalArgumentException 如果指定的初始化容量为负数或者加载因子为非正数
   */
  public HashMap(int initialCapacity, float loadFactor) {
    //初始容量小于0，抛出非法数据异常
    if (initialCapacity < 0) {
      throw new IllegalArgumentException("Illegal initial capacity: " +
          initialCapacity);
    }
    //初始容量最大为MAXIMUM_CAPACITY
    if (initialCapacity > MAXIMUM_CAPACITY) {
      initialCapacity = MAXIMUM_CAPACITY;
    }
    //负载因子必须大于0，并且是合法数字
    if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
      throw new IllegalArgumentException("Illegal load factor: " +
          loadFactor);
    }
    this.loadFactor = loadFactor;
    //将初始容量转成2次幂
    this.threshold = tableSizeFor(initialCapacity);
  }

  /**
   * Constructs an empty <tt>HashMap</tt> with the specified initial
   * capacity and the default load factor (0.75).
   *
   * @param initialCapacity the initial capacity.
   * @throws IllegalArgumentException if the initial capacity is negative.
   */
  /**
   * 使用指定的初始化容量initial capacity和默认加载因子DEFAULT_LOAD_FACTOR（0.75）构造一个空HashMap
   *
   * @param initialCapacity 初始化容量
   * @throws IllegalArgumentException 如果指定的初始化容量为负数
   */
  public HashMap(int initialCapacity) {
    this(initialCapacity, DEFAULT_LOAD_FACTOR);
  }

  /**
   * Constructs an empty <tt>HashMap</tt> with the default initial capacity
   * (16) and the default load factor (0.75).
   */
  /**
   * 使用指定的初始化容量（16）和默认加载因子DEFAULT_LOAD_FACTOR（0.75）构造一个空HashMap，初始容量在第一次put时才会初始化
   */
  public HashMap() {
    this.loadFactor = DEFAULT_LOAD_FACTOR; // all other fields defaulted
  }

  /**
   * Constructs a new <tt>HashMap</tt> with the same mappings as the
   * specified <tt>Map</tt>.  The <tt>HashMap</tt> is created with
   * default load factor (0.75) and an initial capacity sufficient to
   * hold the mappings in the specified <tt>Map</tt>.
   *
   * @param m the map whose mappings are to be placed in this map
   * @throws NullPointerException if the specified map is null
   */
  /**
   * 使用指定Map m构造新的HashMap。使用指定的初始化容量（16）和默认加载因子DEFAULT_LOAD_FACTOR（0.75）
   *
   * @param m 指定的map
   * @throws NullPointerException 如果指定的map是null
   */
  public HashMap(Map<? extends K, ? extends V> m) {
    this.loadFactor = DEFAULT_LOAD_FACTOR;
    putMapEntries(m, false);
  }

  /**
   * Implements Map.putAll and Map constructor
   *
   * @param m     the map
   * @param evict false when initially constructing this map, else true (relayed to method
   *              afterNodeInsertion).
   */
  /**
   * Map.putAll and Map constructor的实现需要的方法
   * 将m的键值对插入本map中
   *
   * @param m     指定的map
   * @param evict 初始化map时使用false，否则使用true
   */
  final void putMapEntries(Map<? extends K, ? extends V> m, boolean evict) {
    int s = m.size();
    //如果参数map不为空
    if (s > 0) {
      // 判断table是否已经初始化
      if (table == null) { // pre-size
        // 未初始化，s为m的实际元素个数
        float ft = ((float) s / loadFactor) + 1.0F;
        int t = ((ft < (float) MAXIMUM_CAPACITY) ?
            (int) ft : MAXIMUM_CAPACITY);
        // 计算得到的t大于阈值，则初始化阈值
        if (t > threshold) {
          //根据容量初始化临界值
          threshold = tableSizeFor(t);
        }
      } else
        // 已初始化，并且m元素个数大于阈值，进行扩容处理
        if (s > threshold) {
          //扩容处理
          resize();
      }

      // 将m中的所有元素添加至HashMap中
      for (Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
        K key = e.getKey();
        V value = e.getValue();
        putVal(hash(key), key, value, false, evict);
      }
    }
  }

  /**
   * Returns the number of key-value mappings in this map.
   *
   * @return the number of key-value mappings in this map
   */
  /**
   * 返回map中键值对映射的个数
   *
   * @return map中键值对映射的个数
   */
  public int size() {
    return size;
  }

  /**
   * Returns <tt>true</tt> if this map contains no key-value mappings.
   *
   * @return <tt>true</tt> if this map contains no key-value mappings
   */
  /**
   * 如果map中没有键值对映射，返回true
   *
   * @return 如果map中没有键值对映射，返回true
   */
  public boolean isEmpty() {
    return size == 0;
  }

  /**
   * Returns the value to which the specified key is mapped,
   * or {@code null} if this map contains no mapping for the key.
   *
   * <p>More formally, if this map contains a mapping from a key
   * {@code k} to a value {@code v} such that {@code (key==null ? k==null :
   * key.equals(k))}, then this method returns {@code v}; otherwise
   * it returns {@code null}.  (There can be at most one such mapping.)
   *
   * <p>A return value of {@code null} does not <i>necessarily</i>
   * indicate that the map contains no mapping for the key; it's also
   * possible that the map explicitly maps the key to {@code null}.
   * The {@link #containsKey containsKey} operation may be used to
   * distinguish these two cases.
   *
   * @see #put(Object, Object)
   */
  /**
   * 返回指定的key映射的value，如果value为null，则返回null
   * get可以分为三个步骤：
   * 1.通过hash(Object key)方法计算key的哈希值hash。
   * 2.通过getNode( int hash, Object key)方法获取node。
   * 3.如果node为null，返回null，否则返回node.value。
   *
   * @see #put(Object, Object)
   */
  public V get(Object key) {
    Node<K, V> e;
    //根据key及其hash值查询node节点，如果存在，则返回该节点的value值
    return (e = getNode(hash(key), key)) == null ? null : e.value;
  }

  /**
   * Implements Map.get and related methods
   *
   * @param hash hash for key
   * @param key  the key
   * @return the node, or null if none
   */
  /**
   * 根据key的哈希值和key获取对应的节点
   * getNode可分为以下几个步骤：
   * 1.如果哈希表为空，或key对应的桶为空，返回null
   * 2.如果桶中的第一个节点就和指定参数hash和key匹配上了，返回这个节点。
   * 3.如果桶中的第一个节点没有匹配上，而且有后续节点
   * 3.1如果当前的桶采用红黑树，则调用红黑树的get方法去获取节点
   * 3.2如果当前的桶不采用红黑树，即桶中节点结构为链式结构，遍历链表，直到key匹配
   * 4.找到节点返回null，否则返回null。
   *
   * @param hash 指定参数key的哈希值
   * @param key  指定参数key
   * @return 返回node，如果没有则返回null
   */
  final Node<K, V> getNode(int hash, Object key) {
    Node<K, V>[] tab;
    Node<K, V> first, e;
    int n;
    K k;
    //如果哈希表不为空，而且key对应的桶上不为空。
    // 注意：直接通过(n - 1) & hash算出下标
    if ((tab = table) != null && (n = tab.length) > 0 &&
        (first = tab[(n - 1) & hash]) != null) {
        //如果桶中的第一个节点就和指定参数hash和key匹配上了
        if (first.hash == hash && // always check first node
            ((k = first.key) == key || (key != null && key.equals(k)))) {
          //返回桶中的第一个节点
          return first;
        }
        //说明有冲突了，遍历tree或list
        if ((e = first.next) != null) {
          //如果当前的桶采用红黑树，则调用红黑树的get方法去获取节点
          if (first instanceof TreeNode) {
            return ((TreeNode<K, V>) first).getTreeNode(hash, key);
          }

          //如果当前的桶不采用红黑树，即桶中节点结构为链式结构
          do {
            //遍历链表，直到key匹配
            if (e.hash == hash &&
                ((k = e.key) == key || (key != null && key.equals(k)))) {
              return e;
            }
          } while ((e = e.next) != null);
        }
    }

    //如果哈希表为空，或者没有找到节点，返回null
    return null;
  }

  /**
   * Returns <tt>true</tt> if this map contains a mapping for the
   * specified key.
   *
   * @param key The key whose presence in this map is to be tested
   * @return <tt>true</tt> if this map contains a mapping for the specified key.
   */
  /**
   * 如果map中含有key为指定参数key的键值对，返回true
   *
   * @param key 指定参数key
   * @return 如果map中含有key为指定参数key的键值对，返回true
   * key.
   */
  public boolean containsKey(Object key) {
    return getNode(hash(key), key) != null;
  }

  /**
   * Associates the specified value with the specified key in this map.
   * If the map previously contained a mapping for the key, the old
   * value is replaced.
   *
   * @param key   key with which the specified value is to be associated
   * @param value value to be associated with the specified key
   * @return the previous value associated with <tt>key</tt>, or <tt>null</tt> if there was no
   * mapping for <tt>key</tt>. (A <tt>null</tt> return can also indicate that the map previously
   * associated <tt>null</tt> with <tt>key</tt>.)
   */
  /**
   * 将指定参数key和指定参数value插入map中，如果key已经存在，那就替换key对应的value
   * put(K key, V value)可以分为三个步骤：
   * 1.通过hash(Object key)方法计算key的哈希值。
   * 2.通过putVal(hash(key), key, value, false, true)方法实现功能。
   * 3.返回putVal方法返回的结果。
   *
   * @param key   指定key
   * @param value 指定value
   * @return 如果value被替换，则返回旧的value，否则返回null。DEFAULT_INITIAL_CAPACITY当然，可能key对应的value就是null
   */
  public V put(K key, V value) {
    return putVal(hash(key), key, value, false, true);
  }

  /**
   * Implements Map.put and related methods
   *
   * @param hash         hash for key
   * @param key          the key
   * @param value        the value to put
   * @param onlyIfAbsent if true, don't change existing value
   * @param evict        if false, the table is in creation mode.
   * @return previous value, or null if none
   */
  /**
   * Map.put和其他相关方法的实现需要的方法
   * putVal方法可以分为下面的几个步骤:
   * 1.如果哈希表为空，调用resize()创建一个哈希表。
   * 2.如果指定参数hash在表中没有对应的桶，即为没有碰撞，直接将键值对插入到哈希表中即可。
   * 3.如果有碰撞，遍历桶，找到key映射的节点
   *    3.1桶中的第一个节点就匹配了，将桶中的第一个节点记录起来。
   *    3.2如果桶中的第一个节点没有匹配，且桶中结构为红黑树，则调用红黑树对应的方法插入键值对。
   *    3.3如果不是红黑树，那么就肯定是链表。遍历链表，如果找到了key映射的节点，就记录这个节点，退出循环。
   *    如果没有找到，在链表尾部插入节点。
   *    插入后，如果链的长度大于TREEIFY_THRESHOLD这个临界值，则使用treeifyBin方法把链表转为红黑树。
   * 4.如果找到了key映射的节点，且节点不为null
   *    4.1记录节点的vlaue。
   *    4.2如果参数onlyIfAbsent为false，或者oldValue为null，替换value，否则不替换。
   *    4.3返回记录下来的节点的value。
   * 5.如果没有找到key映射的节点（2、3步中讲了，这种情况会插入到hashMap中），插入节点后size会加1，
   * 这时要检查size是否大于临界值threshold，如果大于会使用resize方法进行扩容。
   *
   * @param hash         指定参数key的哈希值
   * @param key          指定参数key
   * @param value        指定参数value
   * @param onlyIfAbsent 如果为true，即使指定参数key在map中已经存在，也不会替换value
   * @param evict        如果为false，数组table在创建模式中
   * @return 如果value被替换，则返回旧的value，否则返回null。当然，可能key对应的value就是null。
   */
  final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
                 boolean evict) {
    Node<K, V>[] tab;
    Node<K, V> p;
    int n, i;

    //如果哈希表为空，调用resize()创建一个哈希表，并用变量n记录哈希表长度
    if ((tab = table) == null || (n = tab.length) == 0) {
        /* 这里调用resize，其实就是第一次put时，对数组进行初始化。*/
        n = (tab = resize()).length;
    }

   /**
    * 如果指定参数hash在表中没有对应的桶，即为没有碰撞.
    * (n - 1) & hash 计算key将被放置的槽位.
    * (n - 1) & hash 本质上是hash % n，位运算更快.
    */
    //如果选定的数组坐标处没有元素，直接放入
    if ((p = tab[i = (n - 1) & hash]) == null) {
        //位置为空，将i位置上赋值一个node对象
        tab[i] = newNode(hash, key, value, null);
    } else { // 桶中已经存在元素， 则进入else
      Node<K, V> e;
      K k;

      //如果链表第一个元素或树的根的key与要插入的数元素key 和 hash值完全相同，覆盖旧值
      if (p.hash == hash &&
          ((k = p.key) == key || (key != null && key.equals(k)))) {
        // 将第一个元素赋值给e，用e来记录
        e = p;

      // 当前桶中无该键值对， 进入else. 此时如果桶是红黑树结构，按照红黑树结构插入，调用putTreeVal
      } else if (p instanceof TreeNode) {
        e = ((TreeNode<K, V>) p).putTreeVal(this, tab, hash, key, value);

      //p与新节点既不完全相同，p也不是treenode的实例， 即桶是链表结构，按照链表结构插入到尾部
      //此时只是hash冲突，并且是链表
      } else {
        //遍历链表，找到合适的处理方式 1插入新节点 2覆盖旧值
        for (int binCount = 0; ; ++binCount) { //一个死循环
              //在链表尾部插入新节点
              if ((e = p.next) == null) { //e=p.next,如果p的next指向为null
                  // 先将新节点插入到　p.next
                  p.next = newNode(hash, key, value, null);

                   //如果冲突的节点数已经达到8个，看是否需要改变冲突节点的存储结构，
                   // treeifyBin首先判断当前hashMap的长度，如果不足64，只进行resize，扩容table，
                   // 如果达到64，那么将冲突的存储结构为红黑树
                  if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                  {
                    //将链表转化为二叉树
                    treeifyBin(tab, hash);
                  }
                  break;
              }

              // 如果链表中有一个节点key和新插入的key重复，则跳出循环。
              // 链表节点的<key, value>与put操作<key, value>相同时，不做重复操作，跳出循环
              // 直白一点就是说，如果遍历过程中链表中的元素与新添加的元素完全相同，则跳出循环
              if (e.hash == hash &&
                  ((k = e.key) == key || (key != null && key.equals(k)))) {
                break;
              }

              //将p中的next赋值给p,即将链表中的下一个node赋值给p， 继续循环遍历链表中的元素
              p = e;
        }
      }

      //这个判断中代码作用为：如果添加的元素产生了hash冲突，那么调用put方法时，会将他在链表中他的上一个元素的值返回
      if (e != null) { // existing mapping for key
          // 记录e的value
          V oldValue = e.value;

          //判断条件成立的话，将oldvalue替换
          if (!onlyIfAbsent || oldValue == null) {
              //为newvalue，返回oldvalue；不成立则不替换，然后返回oldvalue
              e.value = value;
          }

          // 访问后回调
          afterNodeAccess(e);

          // 返回旧值
          return oldValue;
      }
    }

    //记录修改次数
    ++modCount;

    // 如果元素数量大于临界值，则进行 rehash 扩容
    // 扩张也是同步判断和执行的，所以恰好触发时会比较慢
    if (++size > threshold) {
      resize();
    }

    // 插入后回调
    afterNodeInsertion(evict);
    return null;
  }

  /**
   * Initializes or doubles table size.  If null, allocates in
   * accord with initial capacity target held in field threshold.
   * Otherwise, because we are using power-of-two expansion, the
   * elements from each bin must either stay at same index, or move
   * with a power of two offset in the new table.
   *
   * @return the table
   */
  /**
   * 对table进行初始化或者扩容。
   * 如果table为null，则对table进行初始化
   * 如果对table扩容，因为每次扩容都是翻倍，与原来计算（n-1）&hash的结果相比，
   * 节点要么就在原来的位置，要么就被分配到“原位置+旧容量”这个位置。
   *
   * resize的步骤总结为:
   * 1.计算扩容后的容量，临界值。
   * 2.将hashMap的临界值修改为扩容后的临界值
   * 3.根据扩容后的容量新建数组，然后将hashMap的table的引用指向新数组。
   * 4.将旧数组的元素复制到table中。
   *
   * @return the table
   */
  final Node<K, V>[] resize() {
    //新建oldTab数组保存扩容前的数组table
    Node<K, V>[] oldTab = table;
    int oldCap = (oldTab == null) ? 0 : oldTab.length;
    //默认构造器的情况下为0
    int oldThr = threshold;
    int newCap, newThr = 0;

    //如果旧表的长度不是空， 扩容肯定执行这个分支
    if (oldCap > 0) {
        //当前table容量大于最大值得时候返回当前table. 此时loadfactor很大，冲突量比较大，查询不再是O(1)
        if (oldCap >= MAXIMUM_CAPACITY) {
          threshold = Integer.MAX_VALUE;
          return oldTab;

        // 把新表的长度设置为旧表长度的两倍，newCap=2*oldCap
        } else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
            oldCap >= DEFAULT_INITIAL_CAPACITY) {
          //扩容容量为2倍，临界值为2倍
          newThr = oldThr << 1; // double threshold
        }

    // 如果旧表的长度的是0，就是说第一次初始化表
    } else if (oldThr > 0) // initial capacity was placed in threshold
    {
      //使用带有初始容量的构造器时，table容量为初始化得到的threshold
      newCap = oldThr;
    } else {               // zero initial threshold signifies using defaults
        //默认构造器下进行扩容
        newCap = DEFAULT_INITIAL_CAPACITY;
        //  默认 16 * 0.75 = 12
        newThr = (int) (DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
    }

    //使用带有初始容量的构造器在此处进行扩容
    if (newThr == 0) {
        //新表长度乘以加载因子
        float ft = (float) newCap * loadFactor;
        newThr = (newCap < MAXIMUM_CAPACITY && ft < (float) MAXIMUM_CAPACITY ?
            (int) ft : Integer.MAX_VALUE);
    }

    //将新的临界值赋值赋值给threshold
    threshold = newThr;

    @SuppressWarnings({"rawtypes", "unchecked"})
    // 开始构造新表， 按newCap创建新的数组，初始化表中的数据
    Node<K, V>[] newTab = (Node<K, V>[]) new Node[newCap];
    //新的数组赋值给table
    table = newTab;

    //扩容后，对新扩容后的table赋值，重新计算元素新的位置。 原表不是空要把原表中数据移动到新表中
    if (oldTab != null) {   // oldCap 原数组
      //遍历原来的旧表
      for (int j = 0; j < oldCap; ++j) {
        Node<K, V> e;

        //判断当前遍历下的该node是否为空，将j位置上的节点保存到e, 然后将oldTab[j]置为空。
        if ((e = oldTab[j]) != null) {
          // 将旧桶置为null. 为什么要置为空，有什么好处？？ TODO
          oldTab[j] = null;

          ///普通节点, 位置是hash求余。 如果为null 说明这个node没有链表直接放在新表的e.hash & (newCap - 1)位置
          if (e.next == null) {
            newTab[e.hash & (newCap - 1)] = e;
          } else if (e instanceof TreeNode) {
            //  树形结构修剪. 当扩容时，
            //  如果当前桶中元素结构是红黑树，并且元素个数小于链表还原阈值 UNTREEIFY_THRESHOLD （默认为 6），就会把桶中的树形结构缩小或者直接还原（切分）为链表结构
            ((TreeNode<K, V>) e).split(this, newTab, j, oldCap);

          // 如果e后边有链表,到这里表示e后面带着个单链表，需要遍历单链表，将每个结点重新计算在新表的位置，并进行搬运
          } else { // preserve order 保证顺序
              Node<K, V> loHead = null, loTail = null;
              Node<K, V> hiHead = null, hiTail = null;
              Node<K, V> next;

              /*
              这里如果判断成立，那么该元素的地址在新的数组中就不会改变。
              因为oldCap的最高位的1，在e.hash对应的位上为0，所以扩容后得到的地址是一样的，位置不会改变 ，在后面的代码的执行中会放到loHead中去，最后赋值给newTab[j]；

              如果判断不成立，那么该元素的地址变为 原下标位置+oldCap，也就是lodCap最高位的1，在e.hash对应的位置上也为1，所以扩容后的地址改变了，在后面的代码中会放到hiHead中，最后赋值给newTab[j + oldCap]

              举个例子来说一下上面的两种情况：
                设：oldCap=16 二进制为：0001 0000
                   oldCap-1=15 二进制为：0000 1111
                   e1.hash=10 二进制为：0000 1010
                   e2.hash=26 二进制为：0101 1010
                e1在扩容前的位置为：e1.hash & oldCap-1  结果为：0000 1010
                e2在扩容前的位置为：e2.hash & oldCap-1  结果为：0000 1010
                结果相同，所以e1和e2在扩容前在同一个链表上，这是扩容之前的状态。

                现在扩容后，需要重新计算元素的位置，在扩容前的链表中计算地址的方式为e.hash & oldCap-1
                那么在扩容后应该也这么计算，扩容后的容量为oldCap*2=32，2^5, 二进制为：0010 0000。 所以 newCap=32，
                新的计算方式应该为
                e1.hash & newCap-1
                即：0000 1010 & 0001 1111
                结果为0000 1010与扩容前的位置完全一样。
                e2.hash & newCap-1 即：0101 1010 & 0001 1111
                结果为0001 1010,为扩容前位置+oldCap。

                而这里却没有e.hash & newCap-1 而是 e.hash & oldCap，其实这两个是等效的，都是判断倒数第五位是0，还是1。
                如果是0，则位置不变，是1则位置改变为扩容前位置+oldCap。

                再来分析下loTail, loHead这两个的执行过程（假设(e.hash & oldCap) == 0成立）：
                第一次执行：
                e指向oldTab[j]所指向的node对象，即e指向该位置上链表的第一个元素.
                loTail为空,所以loHead指向与e相同的node对象（loHead = e;），然后loTail也指向了同一个node对象（loTail = e;）。
                最后，在判断条件e指向next，就是指向oldTab链表中的第二个元素

                第二次执行：
                lotail不为null，所以lotail.next指向e，这里其实是lotail指向的node对象的next指向e，
                也可以说是，loHead的next指向了e，就是指向了oldTab链表中第二个元素。此时loHead指向
                的node变成了一个长度为2的链表。然后lotail=e也就是指向了链表中第二个元素的地址。

                第三次执行：
                与第二次执行类似，loHead上的链表长度变为3，又增加了一个node，loTail指向新增的node......

                hiTail与hiHead的执行过程与以上相同。
                由此可以看出，loHead是用来保存新链表上的头元素的，loTail是用来保存尾元素的，直到遍历完链表。
                这是(e.hash & oldCap) == 0成立的时候。

                (e.hash & oldCap) == 0不成立的情况也相同，其实就是把oldCap遍历成两个新的链表，
                通过loHead和hiHead来保存链表的头结点，然后将两个头结点放到newTab[j]与newTab[j+oldCap]上面去。
              */
              do {
                //记录下一个结点
                next = e.next;

                // 新表是旧表的两倍容量，实例上就把单链表拆分为两队, e.hash&oldCap==0为偶数一队，反之为奇数一对
                if ((e.hash & oldCap) == 0) {
                  if (loTail == null) {
                    loHead = e;
                  } else {
                    loTail.next = e;
                  }
                  loTail = e;
                } else {
                  if (hiTail == null) {
                    hiHead = e;
                  } else {
                    hiTail.next = e;
                  }
                  hiTail = e;
                }
              } while ((e = next) != null);

              //lo队不为null，放在新表原位置
              if (loTail != null) {
                //尾节点的next设置为空
                loTail.next = null;
                newTab[j] = loHead;
              }

              //hi队不为null，放在新表j+oldCap位置
              if (hiTail != null) {
                //尾节点的next设置为空
                hiTail.next = null;
                newTab[j + oldCap] = hiHead;
              }
          }
        }
      }
    }
    return newTab;
  }

  /**
   * Replaces all linked nodes in bin at index for given hash unless
   * table is too small, in which case resizes instead.
   */
  /**
   * 将链表转化为红黑树
   */
  final void treeifyBin(Node<K, V>[] tab, int hash) {
    int n, index;
    Node<K, V> e;
    //如果桶数组table为空，或者桶数组table的长度小于MIN_TREEIFY_CAPACITY，不符合转化为红黑树的条件
    if (tab == null || (n = tab.length) < MIN_TREEIFY_CAPACITY) {
      //扩容
      resize();
    } else
      // 如果符合转化为红黑树的条件，而且hash对应的桶不为null， 则重新计算 hash段位，及table的索引位，第一个节点
      if ((e = tab[index = (n - 1) & hash]) != null) {
           /************　双向链表　start***************/
          //　红黑树的hd头节点, tl尾节点
          TreeNode<K, V> hd = null, tl = null;
          //遍历链表
          do {
            //替换链表node为树node，建立双向链表
            TreeNode<K, V> p = replacementTreeNode(e, null);
            // 确定树头节点
            if (tl == null) {
              hd = p;
            } else {
              p.prev = tl;
              tl.next = p;
            }
            tl = p;
          } while ((e = e.next) != null);
          /************　双向链表　end***************/


          // 前面仅仅转换为双向链表，treeify才是转换红黑树的处理方法入口　
          // 让桶的第一个元素指向新建的红黑树头结点，以后这个桶里的元素就是红黑树而不是链表了
          if ((tab[index] = hd) != null) {
            // 将二叉树转换为红黑树
            hd.treeify(tab);
          }
    }
  }

  /**
   * Copies all of the mappings from the specified map to this map.
   * These mappings will replace any mappings that this map had for
   * any of the keys currently in the specified map.
   *
   * @param m mappings to be stored in this map
   * @throws NullPointerException if the specified map is null
   */
  /**
   * 将参数map中的所有键值对映射插入到hashMap中，如果有碰撞，则覆盖value。
   *
   * @param m 参数map
   * @throws NullPointerException 如果map为null
   */
  public void putAll(Map<? extends K, ? extends V> m) {
    putMapEntries(m, true);
  }

  /**
   * Removes the mapping for the specified key from this map if present.
   *
   * @param key key whose mapping is to be removed from the map
   * @return the previous value associated with <tt>key</tt>, or <tt>null</tt> if there was no
   * mapping for <tt>key</tt>. (A <tt>null</tt> return can also indicate that the map previously
   * associated <tt>null</tt> with <tt>key</tt>.)
   */
  /**
   * 删除hashMap中key映射的node
   * remove方法的实现可以分为三个步骤：
   * 1.通过 hash(Object key)方法计算key的哈希值。
   * 2.通过 removeNode 方法实现功能。
   * 3.返回被删除的node的value。
   *
   * @param key 参数key
   * @return 如果没有映射到node，返回null，否则返回对应的value
   */
  public V remove(Object key) {
    Node<K, V> e;
    //根据key来删除node。removeNode方法的具体实现在下面
    return (e = removeNode(hash(key), key, null, false, true)) == null ?
        null : e.value;
  }

  /**
   * Implements Map.remove and related methods
   *
   * @param hash       hash for key
   * @param key        the key
   * @param value      the value to match if matchValue, else ignored
   * @param matchValue if true only remove if value is equal
   * @param movable    if false do not move other nodes while removing
   * @return the node, or null if none
   */
  /**
   * Map.remove和相关方法的实现需要的方法
   * removeNode方法的步骤总结为:
   * 1.如果数组table为空或key映射到的桶为空，返回null。
   * 2.如果key映射到的桶上第一个node的就是要删除的node，记录下来。
   * 3.如果桶内不止一个node，且桶内的结构为红黑树，记录key映射到的node。
   * 4.桶内的结构不为红黑树，那么桶内的结构就肯定为链表，遍历链表，找到key映射到的node，记录下来。
   * 5.如果被记录下来的node不为null，删除node，size-1被删除。
   * 6.返回被删除的node。
   *
   * @param hash       key的哈希值
   * @param key        key的哈希值
   * @param value      如果 matchValue 为true，则value也作为确定被删除的node的条件之一，否则忽略
   * @param matchValue 如果为true，则value也作为确定被删除的node的条件之一
   * @param movable    如果为false，删除node时不会删除其他node
   * @return 返回被删除的node，如果没有node被删除，则返回null（针对红黑树的删除方法）
   */
  final Node<K, V> removeNode(int hash, Object key, Object value,
                              boolean matchValue, boolean movable) {
    Node<K, V>[] tab;
    Node<K, V> p;
    int n, index;

    //如果数组table不为空且key映射到的桶不为空
    if ((tab = table) != null && (n = tab.length) > 0 &&
        (p = tab[index = (n - 1) & hash]) != null) {
      Node<K, V> node = null, e;
      K k;
      V v;

      //如果桶上第一个node的就是要删除的node
      if (p.hash == hash &&
          ((k = p.key) == key || (key != null && key.equals(k)))) {
        //记录桶上第一个node
        node = p;
      } else if ((e = p.next) != null) {//如果桶内不止一个node
        //如果桶内的结构为红黑树
        if (p instanceof TreeNode) {
          //记录key映射到的node
          node = ((TreeNode<K, V>) p).getTreeNode(hash, key);
        } else {//如果桶内的结构为链表
          do {//遍历链表，找到key映射到的node
            if (e.hash == hash &&
                ((k = e.key) == key ||
                    (key != null && key.equals(k)))) {
              //记录key映射到的node
              node = e;
              break;
            }
            p = e;
          } while ((e = e.next) != null);
        }
      }

      //如果得到的node不为null且(matchValue为false||node.value和参数value匹配)
      if (node != null && (!matchValue || (v = node.value) == value ||
              (value != null && value.equals(v)))) {
        //如果桶内的结构为红黑树
        if (node instanceof TreeNode)
          //使用红黑树的删除方法删除node
          ((TreeNode<K, V>) node).removeTreeNode(this, tab, movable);
        else if (node == p)//如果桶的第一个node的就是要删除的node
          //删除node
          tab[index] = node.next;
        else//如果桶内的结构为链表，使用链表删除元素的方式删除node
          p.next = node.next;
        ++modCount;//结构性修改次数+1
        --size;//哈希表大小-1
        afterNodeRemoval(node);
        return node;//返回被删除的node
      }
    }

    //如果数组table为空或key映射到的桶为空，返回null。
    return null;
  }

  /**
   * Removes all of the mappings from this map.
   * The map will be empty after this call returns.
   */
  /**
   * 删除map中所有的键值对
   */
  public void clear() {
    Node<K, V>[] tab;
    modCount++;
    if ((tab = table) != null && size > 0) {
      size = 0;
      for (int i = 0; i < tab.length; ++i) {
        tab[i] = null;
      }
    }
  }

  /**
   * Returns <tt>true</tt> if this map maps one or more keys to the
   * specified value.
   *
   * @param value value whose presence in this map is to be tested
   * @return <tt>true</tt> if this map maps one or more keys to the specified value
   */
  /**
   * 如果hashMap中的键值对有一对或多对的value为参数value，返回true
   *
   * @param value 参数value
   * @return 如果hashMap中的键值对有一对或多对的value为参数value，返回true
   */
  public boolean containsValue(Object value) {
    Node<K, V>[] tab;
    V v;
    if ((tab = table) != null && size > 0) {
      //遍历数组table
      for (int i = 0; i < tab.length; ++i) {
        //遍历桶中的node
        for (Node<K, V> e = tab[i]; e != null; e = e.next) {
          if ((v = e.value) == value ||
              (value != null && value.equals(v))) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * Returns a {@link Set} view of the keys contained in this map.
   * The set is backed by the map, so changes to the map are
   * reflected in the set, and vice-versa.  If the map is modified
   * while an iteration over the set is in progress (except through
   * the iterator's own <tt>remove</tt> operation), the results of
   * the iteration are undefined.  The set supports element removal,
   * which removes the corresponding mapping from the map, via the
   * <tt>Iterator.remove</tt>, <tt>Set.remove</tt>,
   * <tt>removeAll</tt>, <tt>retainAll</tt>, and <tt>clear</tt>
   * operations.  It does not support the <tt>add</tt> or <tt>addAll</tt>
   * operations.
   *
   * @return a set view of the keys contained in this map
   */
  /**
   * 返回hashMap中所有key的视图。
   * 改变hashMap会影响到set，反之亦然。
   * 如果当迭代器迭代set时，hashMap被修改(除非是迭代器自己的remove()方法)，迭代器的结果是不确定的。
   * set支持元素的删除，通过Iterator.remove、Set.remove、removeAll、retainAll、clear操作删除hashMap中对应的键值对。
   * 不支持add和addAll方法。
   *
   * @return 返回hashMap中所有key的set视图
   */
  public Set<K> keySet() {
    Set<K> ks;
    return (ks = keySet) == null ? (keySet = new KeySet()) : ks;
  }

  /**
   * 内部类KeySet
   */
  final class KeySet extends AbstractSet<K> {

    public final int size() {
      return size;
    }

    public final void clear() {
      /// 注意会clear掉map
      HashMap.this.clear();
    }

    public final Iterator<K> iterator() {
      return new KeyIterator();
    }

    public final boolean contains(Object o) {
      return containsKey(o);
    }

    public final boolean remove(Object key) {
      return removeNode(hash(key), key, null, false, true) != null;
    }

    public final Spliterator<K> spliterator() {
      return new KeySpliterator<>(HashMap.this, 0, -1, 0, 0);
    }

    public final void forEach(Consumer<? super K> action) {
      Node<K, V>[] tab;
      if (action == null) {
        throw new NullPointerException();
      }
      if (size > 0 && (tab = table) != null) {
        int mc = modCount;
        for (int i = 0; i < tab.length; ++i) {
          for (Node<K, V> e = tab[i]; e != null; e = e.next) {
            action.accept(e.key);
          }
        }
        if (modCount != mc) {
          throw new ConcurrentModificationException();
        }
      }
    }
  }

  /**
   * Returns a {@link Collection} view of the values contained in this map.
   * The collection is backed by the map, so changes to the map are
   * reflected in the collection, and vice-versa.  If the map is
   * modified while an iteration over the collection is in progress
   * (except through the iterator's own <tt>remove</tt> operation),
   * the results of the iteration are undefined.  The collection
   * supports element removal, which removes the corresponding
   * mapping from the map, via the <tt>Iterator.remove</tt>,
   * <tt>Collection.remove</tt>, <tt>removeAll</tt>,
   * <tt>retainAll</tt> and <tt>clear</tt> operations.  It does not
   * support the <tt>add</tt> or <tt>addAll</tt> operations.
   *
   * @return a view of the values contained in this map
   */
  /**
   * 返回hashMap中所有value的collection视图
   * 改变hashMap会改变collection，反之亦然。
   * 如果当迭代器迭代collection时，hashMap被修改（除非是迭代器自己的remove()方法），迭代器的结果是不确定的。
   * collection支持元素的删除，通过Iterator.remove、Collection.remove、removeAll、retainAll、clear操作删除hashMap中对应的键值对。
   * 不支持add和addAll方法。
   *
   * @return 返回hashMap中所有key的collection视图
   */
  public Collection<V> values() {
    Collection<V> vs;
    return (vs = values) == null ? (values = new Values()) : vs;
  }

  /**
   * 内部类Values
   */
  final class Values extends AbstractCollection<V> {

    public final int size() {
      return size;
    }

    public final void clear() {
      HashMap.this.clear();
    }

    public final Iterator<V> iterator() {
      return new ValueIterator();
    }

    public final boolean contains(Object o) {
      return containsValue(o);
    }

    public final Spliterator<V> spliterator() {
      return new ValueSpliterator<>(HashMap.this, 0, -1, 0, 0);
    }

    public final void forEach(Consumer<? super V> action) {
      Node<K, V>[] tab;
      if (action == null) {
        throw new NullPointerException();
      }
      if (size > 0 && (tab = table) != null) {
        int mc = modCount;
        for (int i = 0; i < tab.length; ++i) {
          for (Node<K, V> e = tab[i]; e != null; e = e.next) {
            action.accept(e.value);
          }
        }
        if (modCount != mc) {
          throw new ConcurrentModificationException();
        }
      }
    }
  }

  /**
   * Returns a {@link Set} view of the mappings contained in this map.
   * The set is backed by the map, so changes to the map are
   * reflected in the set, and vice-versa.  If the map is modified
   * while an iteration over the set is in progress (except through
   * the iterator's own <tt>remove</tt> operation, or through the
   * <tt>setValue</tt> operation on a map entry returned by the
   * iterator) the results of the iteration are undefined.  The set
   * supports element removal, which removes the corresponding
   * mapping from the map, via the <tt>Iterator.remove</tt>,
   * <tt>Set.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt> and
   * <tt>clear</tt> operations.  It does not support the
   * <tt>add</tt> or <tt>addAll</tt> operations.
   *
   * @return a set view of the mappings contained in this map
   */
  public Set<Map.Entry<K, V>> entrySet() {
    Set<Map.Entry<K, V>> es;
    return (es = entrySet) == null ? (entrySet = new EntrySet()) : es;
  }

  /**
   * 内部类EntrySet
   */
  final class EntrySet extends AbstractSet<Map.Entry<K, V>> {

    public final int size() {
      return size;
    }

    public final void clear() {
      HashMap.this.clear();
    }

    public final Iterator<Map.Entry<K, V>> iterator() {
      return new EntryIterator();
    }

    public final boolean contains(Object o) {
      if (!(o instanceof Map.Entry)) {
        return false;
      }
      Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
      Object key = e.getKey();
      Node<K, V> candidate = getNode(hash(key), key);
      return candidate != null && candidate.equals(e);
    }

    public final boolean remove(Object o) {
      if (o instanceof Map.Entry) {
        Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
        Object key = e.getKey();
        Object value = e.getValue();
        return removeNode(hash(key), key, value, true, true) != null;
      }
      return false;
    }

    public final Spliterator<Map.Entry<K, V>> spliterator() {
      return new EntrySpliterator<>(HashMap.this, 0, -1, 0, 0);
    }

    public final void forEach(Consumer<? super Map.Entry<K, V>> action) {
      Node<K, V>[] tab;
      if (action == null) {
        throw new NullPointerException();
      }
      if (size > 0 && (tab = table) != null) {
        int mc = modCount;
        for (int i = 0; i < tab.length; ++i) {
          for (Node<K, V> e = tab[i]; e != null; e = e.next) {
            action.accept(e);
          }
        }
        if (modCount != mc) {
          throw new ConcurrentModificationException();
        }
      }
    }
  }

  // Overrides of JDK8 Map extension methods
  // JDK8重写的方法

  /**
   * 通过key映射到对应node，如果没映射到则返回默认值defaultValue
   *
   * @param key
   * @param defaultValue
   * @return key映射到对应的node，如果没映射到则返回默认值defaultValue
   */
  @Override
  public V getOrDefault(Object key, V defaultValue) {
    Node<K, V> e;
    return (e = getNode(hash(key), key)) == null ? defaultValue : e.value;
  }

  /**
   * 在hashMap中插入参数key和value组成的键值对，如果key在hashMap中已经存在，不替换value
   *
   * @param key
   * @param value
   * @return 如果key在hashMap中不存在，返回旧value
   */
  @Override
  public V putIfAbsent(K key, V value) {
    return putVal(hash(key), key, value, true, true);
  }

  /**
   * 删除hashMap中key为参数key，value为参数value的键值对。如果桶中结构为树，则级联删除
   *
   * @param key
   * @param value
   * @return 删除成功，返回true
   */
  @Override
  public boolean remove(Object key, Object value) {
    return removeNode(hash(key), key, value, true, true) != null;
  }

  /**
   * 使用newValue替换key和oldValue映射到的键值对中的value
   *
   * @param key
   * @param oldValue
   * @param newValue
   * @return 替换成功，返回true
   */
  @Override
  public boolean replace(K key, V oldValue, V newValue) {
    Node<K, V> e;
    V v;
    if ((e = getNode(hash(key), key)) != null &&
        ((v = e.value) == oldValue || (v != null && v.equals(oldValue)))) {
      e.value = newValue;
      afterNodeAccess(e);
      return true;
    }
    return false;
  }

  /**
   * 使用参数value替换key映射到的键值对中的value
   *
   * @param key
   * @param value
   * @return 替换成功，返回true
   */
  @Override
  public V replace(K key, V value) {
    Node<K, V> e;
    if ((e = getNode(hash(key), key)) != null) {
      V oldValue = e.value;
      e.value = value;
      afterNodeAccess(e);
      return oldValue;
    }
    return null;
  }

  @Override
  public V computeIfAbsent(K key,
                           Function<? super K, ? extends V> mappingFunction) {
    if (mappingFunction == null) {
      throw new NullPointerException();
    }
    int hash = hash(key);
    Node<K, V>[] tab;
    Node<K, V> first;
    int n, i;
    int binCount = 0;
    TreeNode<K, V> t = null;
    Node<K, V> old = null;
    if (size > threshold || (tab = table) == null ||
        (n = tab.length) == 0) {
      n = (tab = resize()).length;
    }
    if ((first = tab[i = (n - 1) & hash]) != null) {
      if (first instanceof TreeNode) {
        old = (t = (TreeNode<K, V>) first).getTreeNode(hash, key);
      } else {
        Node<K, V> e = first;
        K k;
        do {
          if (e.hash == hash &&
              ((k = e.key) == key || (key != null && key.equals(k)))) {
            old = e;
            break;
          }
          ++binCount;
        } while ((e = e.next) != null);
      }
      V oldValue;
      if (old != null && (oldValue = old.value) != null) {
        afterNodeAccess(old);
        return oldValue;
      }
    }
    V v = mappingFunction.apply(key);
    if (v == null) {
      return null;
    } else if (old != null) {
      old.value = v;
      afterNodeAccess(old);
      return v;
    } else if (t != null) {
      t.putTreeVal(this, tab, hash, key, v);
    } else {
      tab[i] = newNode(hash, key, v, first);
      if (binCount >= TREEIFY_THRESHOLD - 1) {
        treeifyBin(tab, hash);
      }
    }
    ++modCount;
    ++size;
    afterNodeInsertion(true);
    return v;
  }

  public V computeIfPresent(K key,
                            BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
    if (remappingFunction == null) {
      throw new NullPointerException();
    }
    Node<K, V> e;
    V oldValue;
    int hash = hash(key);
    if ((e = getNode(hash, key)) != null &&
        (oldValue = e.value) != null) {
      V v = remappingFunction.apply(key, oldValue);
      if (v != null) {
        e.value = v;
        afterNodeAccess(e);
        return v;
      } else {
        removeNode(hash, key, null, false, true);
      }
    }
    return null;
  }

  @Override
  public V compute(K key,
                   BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
    if (remappingFunction == null) {
      throw new NullPointerException();
    }
    int hash = hash(key);
    Node<K, V>[] tab;
    Node<K, V> first;
    int n, i;
    int binCount = 0;
    TreeNode<K, V> t = null;
    Node<K, V> old = null;
    if (size > threshold || (tab = table) == null ||
        (n = tab.length) == 0) {
      n = (tab = resize()).length;
    }
    if ((first = tab[i = (n - 1) & hash]) != null) {
      if (first instanceof TreeNode) {
        old = (t = (TreeNode<K, V>) first).getTreeNode(hash, key);
      } else {
        Node<K, V> e = first;
        K k;
        do {
          if (e.hash == hash &&
              ((k = e.key) == key || (key != null && key.equals(k)))) {
            old = e;
            break;
          }
          ++binCount;
        } while ((e = e.next) != null);
      }
    }
    V oldValue = (old == null) ? null : old.value;
    V v = remappingFunction.apply(key, oldValue);
    if (old != null) {
      if (v != null) {
        old.value = v;
        afterNodeAccess(old);
      } else {
        removeNode(hash, key, null, false, true);
      }
    } else if (v != null) {
      if (t != null) {
        t.putTreeVal(this, tab, hash, key, v);
      } else {
        tab[i] = newNode(hash, key, v, first);
        if (binCount >= TREEIFY_THRESHOLD - 1) {
          treeifyBin(tab, hash);
        }
      }
      ++modCount;
      ++size;
      afterNodeInsertion(true);
    }
    return v;
  }

  @Override
  public V merge(K key, V value,
                 BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
    if (value == null) {
      throw new NullPointerException();
    }
    if (remappingFunction == null) {
      throw new NullPointerException();
    }
    int hash = hash(key);
    Node<K, V>[] tab;
    Node<K, V> first;
    int n, i;
    int binCount = 0;
    TreeNode<K, V> t = null;
    Node<K, V> old = null;
    if (size > threshold || (tab = table) == null ||
        (n = tab.length) == 0) {
      n = (tab = resize()).length;
    }
    if ((first = tab[i = (n - 1) & hash]) != null) {
      if (first instanceof TreeNode) {
        old = (t = (TreeNode<K, V>) first).getTreeNode(hash, key);
      } else {
        Node<K, V> e = first;
        K k;
        do {
          if (e.hash == hash &&
              ((k = e.key) == key || (key != null && key.equals(k)))) {
            old = e;
            break;
          }
          ++binCount;
        } while ((e = e.next) != null);
      }
    }
    if (old != null) {
      V v;
      if (old.value != null) {
        v = remappingFunction.apply(old.value, value);
      } else {
        v = value;
      }
      if (v != null) {
        old.value = v;
        afterNodeAccess(old);
      } else {
        removeNode(hash, key, null, false, true);
      }
      return v;
    }
    if (value != null) {
      if (t != null) {
        t.putTreeVal(this, tab, hash, key, value);
      } else {
        tab[i] = newNode(hash, key, value, first);
        if (binCount >= TREEIFY_THRESHOLD - 1) {
          treeifyBin(tab, hash);
        }
      }
      ++modCount;
      ++size;
      afterNodeInsertion(true);
    }
    return value;
  }

  @Override
  public void forEach(BiConsumer<? super K, ? super V> action) {
    Node<K, V>[] tab;
    if (action == null) {
      throw new NullPointerException();
    }
    if (size > 0 && (tab = table) != null) {
      int mc = modCount;
      for (int i = 0; i < tab.length; ++i) {
        for (Node<K, V> e = tab[i]; e != null; e = e.next) {
          action.accept(e.key, e.value);
        }
      }
      if (modCount != mc) {
        throw new ConcurrentModificationException();
      }
    }
  }

  @Override
  public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
    Node<K, V>[] tab;
    if (function == null) {
      throw new NullPointerException();
    }
    if (size > 0 && (tab = table) != null) {
      int mc = modCount;
      for (int i = 0; i < tab.length; ++i) {
        for (Node<K, V> e = tab[i]; e != null; e = e.next) {
          e.value = function.apply(e.key, e.value);
        }
      }
      if (modCount != mc) {
        throw new ConcurrentModificationException();
      }
    }
  }

    /* ------------------------------------------------------------ */
  // Cloning and serialization
  // 克隆和序列化

  /**
   * Returns a shallow copy of this <tt>HashMap</tt> instance: the keys and
   * values themselves are not cloned.
   *
   * @return a shallow copy of this map
   */
  /**
   * 浅拷贝。
   * clone方法虽然生成了新的HashMap对象，新的HashMap中的table数组虽然也是新生成的，但是数组中的元素还是引用以前的HashMap中的元素。
   * 这就导致在对HashMap中的元素进行修改的时候，即对数组中元素进行修改，会导致原对象和clone对象都发生改变，
   * 但进行新增或删除就不会影响对方，因为这相当于是对数组做出的改变，clone对象新生成了一个数组。
   *
   * @return hashMap的浅拷贝
   */
  @SuppressWarnings("unchecked")
  @Override
  public Object clone() {
    HashMap<K, V> result;
    try {
      result = (HashMap<K, V>) super.clone();
    } catch (CloneNotSupportedException e) {
      // this shouldn't happen, since we are Cloneable
      throw new InternalError(e);
    }
    result.reinitialize();
    result.putMapEntries(this, false);
    return result;
  }

  // These methods are also used when serializing HashSets
  final float loadFactor() {
    return loadFactor;
  }

  final int capacity() {
    return (table != null) ? table.length :
        (threshold > 0) ? threshold :
            DEFAULT_INITIAL_CAPACITY;
  }

  /**
   * Save the state of the <tt>HashMap</tt> instance to a stream (i.e.,
   * serialize it).
   *
   * @serialData The <i>capacity</i> of the HashMap (the length of the bucket array) is emitted
   * (int), followed by the <i>size</i> (an int, the number of key-value mappings), followed by the
   * key (Object) and value (Object) for each key-value mapping.  The key-value mappings are emitted
   * in no particular order.
   */
  /**
   * 序列化hashMap到ObjectOutputStream中
   * 将hashMap的总容量capacity、实际容量size、键值对映射写入到ObjectOutputStream中。键值对映射序列化时是无序的。
   *
   * @serialData The <i>capacity</i> of the HashMap (the length of the
   * bucket array) is emitted (int), followed by the
   * <i>size</i> (an int, the number of key-value
   * mappings), followed by the key (Object) and value (Object)
   * for each key-value mapping.  The key-value mappings are
   * emitted in no particular order.
   */
  private void writeObject(java.io.ObjectOutputStream s)
      throws IOException {
    int buckets = capacity();
    // Write out the threshold, loadfactor, and any hidden stuff
    s.defaultWriteObject();
    //写入总容量
    s.writeInt(buckets);
    //写入实际容量
    s.writeInt(size);
    //写入键值对
    internalWriteEntries(s);
  }

  /**
   * Reconstitute the {@code HashMap} instance from a stream (i.e.,
   * deserialize it).
   */
  /**
   * 到ObjectOutputStream中读取hashMap
   * 将hashMap的总容量capacity、实际容量size、键值对映射读取出来
   */
  private void readObject(java.io.ObjectInputStream s)
      throws IOException, ClassNotFoundException {
    // Read in the threshold (ignored), loadfactor, and any hidden stuff
    // 将hashMap的总容量capacity、实际容量size、键值对映射读取出来
    s.defaultReadObject();
    //重置hashMap
    reinitialize();

    //如果加载因子不合法，抛出异常
    if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
      throw new InvalidObjectException("Illegal load factor: " +
          loadFactor);
    }
    s.readInt();                // Read and ignore number of buckets. 读出桶的数量，忽略
    int mappings = s.readInt(); // Read number of mappings (size).  读出实际容量size
    //如果读出的实际容量size小于0，抛出异常
    if (mappings < 0) {
      throw new InvalidObjectException("Illegal mappings count: " +
          mappings);
    } else if (mappings > 0) { // (if zero, use defaults)
      // Size the table using given load factor only if within
      // range of 0.25...4.0
      //调整hashMap大小
      float lf = Math.min(Math.max(0.25f, loadFactor), 4.0f);// 加载因子
      float fc = (float) mappings / lf + 1.0f;         //初步得到的总容量，后续还会处理
      //处理初步得到的容量，确认最终的总容量
      int cap = ((fc < DEFAULT_INITIAL_CAPACITY) ?
          DEFAULT_INITIAL_CAPACITY :
          (fc >= MAXIMUM_CAPACITY) ?
              MAXIMUM_CAPACITY :
              tableSizeFor((int) fc));
      //计算临界值，得到初步的临界值
      float ft = (float) cap * lf;
      //得到最终的临界值
      threshold = ((cap < MAXIMUM_CAPACITY && ft < MAXIMUM_CAPACITY) ?
          (int) ft : Integer.MAX_VALUE);
      @SuppressWarnings({"rawtypes", "unchecked"})
      //新建桶数组table
      Node<K, V>[] tab = (Node<K, V>[]) new Node[cap];
      table = tab;

      // 读出key和value，并组成键值对插入hashMap中
      // Read the keys and values, and put the mappings in the HashMap
      for (int i = 0; i < mappings; i++) {
        @SuppressWarnings("unchecked")
        K key = (K) s.readObject();
        @SuppressWarnings("unchecked")
        V value = (V) s.readObject();
        putVal(hash(key), key, value, false, false);
      }
    }
  }

    /* ------------------------------------------------------------ */
  // iterators

  abstract class HashIterator {

    Node<K, V> next;        // next entry to return
    Node<K, V> current;     // current entry
    int expectedModCount;  // for fast-fail
    int index;             // current slot

    HashIterator() {
      expectedModCount = modCount;
      Node<K, V>[] t = table;
      current = next = null;
      index = 0;
      if (t != null && size > 0) { // advance to first entry
        do {
        } while (index < t.length && (next = t[index++]) == null);
      }
    }

    public final boolean hasNext() {
      return next != null;
    }

    final Node<K, V> nextNode() {
      Node<K, V>[] t;
      Node<K, V> e = next;
      if (modCount != expectedModCount) {
        throw new ConcurrentModificationException();
      }
      if (e == null) {
        throw new NoSuchElementException();
      }
      if ((next = (current = e).next) == null && (t = table) != null) {
        do {
        } while (index < t.length && (next = t[index++]) == null);
      }
      return e;
    }

    public final void remove() {
      Node<K, V> p = current;
      if (p == null) {
        throw new IllegalStateException();
      }
      if (modCount != expectedModCount) {
        throw new ConcurrentModificationException();
      }
      current = null;
      K key = p.key;
      removeNode(hash(key), key, null, false, false);
      expectedModCount = modCount;
    }
  }

  final class KeyIterator extends HashIterator
      implements Iterator<K> {

    public final K next() {
      return nextNode().key;
    }
  }

  final class ValueIterator extends HashIterator
      implements Iterator<V> {

    public final V next() {
      return nextNode().value;
    }
  }

  final class EntryIterator extends HashIterator
      implements Iterator<Map.Entry<K, V>> {

    public final Map.Entry<K, V> next() {
      return nextNode();
    }
  }

    /* ------------------------------------------------------------ */
  // spliterators

  static class HashMapSpliterator<K, V> {

    final HashMap<K, V> map;
    Node<K, V> current;          //记录当前的节点
    int index;                  //当前节点的下标
    int fence;                  //堆大小
    int est;                    //估计大小
    int expectedModCount;       // for comodification checks

    HashMapSpliterator(HashMap<K, V> m, int origin,
                       int fence, int est,
                       int expectedModCount) {
      this.map = m;
      this.index = origin;
      this.fence = fence;
      this.est = est;
      this.expectedModCount = expectedModCount;
    }

    final int getFence() { // initialize fence and size on first use
      int hi;
      if ((hi = fence) < 0) {
        HashMap<K, V> m = map;
        est = m.size;
        expectedModCount = m.modCount;
        Node<K, V>[] tab = m.table;
        hi = fence = (tab == null) ? 0 : tab.length;
      }
      return hi;
    }

    public final long estimateSize() {
      getFence(); // force init
      return (long) est;
    }
  }

  static final class KeySpliterator<K, V>
      extends HashMapSpliterator<K, V>
      implements Spliterator<K> {

    KeySpliterator(HashMap<K, V> m, int origin, int fence, int est,
                   int expectedModCount) {
      super(m, origin, fence, est, expectedModCount);
    }

    public KeySpliterator<K, V> trySplit() {
      int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
      return (lo >= mid || current != null) ? null :
          new KeySpliterator<>(map, lo, index = mid, est >>>= 1,
              expectedModCount);
    }

    public void forEachRemaining(Consumer<? super K> action) {
      int i, hi, mc;
      if (action == null) {
        throw new NullPointerException();
      }
      HashMap<K, V> m = map;
      Node<K, V>[] tab = m.table;
      if ((hi = fence) < 0) {
        mc = expectedModCount = m.modCount;
        hi = fence = (tab == null) ? 0 : tab.length;
      } else {
        mc = expectedModCount;
      }
      if (tab != null && tab.length >= hi &&
          (i = index) >= 0 && (i < (index = hi) || current != null)) {
        Node<K, V> p = current;
        current = null;
        do {
          if (p == null) {
            p = tab[i++];
          } else {
            action.accept(p.key);
            p = p.next;
          }
        } while (p != null || i < hi);
        if (m.modCount != mc) {
          throw new ConcurrentModificationException();
        }
      }
    }

    public boolean tryAdvance(Consumer<? super K> action) {
      int hi;
      if (action == null) {
        throw new NullPointerException();
      }
      Node<K, V>[] tab = map.table;
      if (tab != null && tab.length >= (hi = getFence()) && index >= 0) {
        while (current != null || index < hi) {
          if (current == null) {
            current = tab[index++];
          } else {
            K k = current.key;
            current = current.next;
            action.accept(k);
            if (map.modCount != expectedModCount) {
              throw new ConcurrentModificationException();
            }
            return true;
          }
        }
      }
      return false;
    }

    public int characteristics() {
      return (fence < 0 || est == map.size ? Spliterator.SIZED : 0) |
          Spliterator.DISTINCT;
    }
  }

  static final class ValueSpliterator<K, V>
      extends HashMapSpliterator<K, V>
      implements Spliterator<V> {

    ValueSpliterator(HashMap<K, V> m, int origin, int fence, int est,
                     int expectedModCount) {
      super(m, origin, fence, est, expectedModCount);
    }

    public ValueSpliterator<K, V> trySplit() {
      int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
      return (lo >= mid || current != null) ? null :
          new ValueSpliterator<>(map, lo, index = mid, est >>>= 1,
              expectedModCount);
    }

    public void forEachRemaining(Consumer<? super V> action) {
      int i, hi, mc;
      if (action == null) {
        throw new NullPointerException();
      }
      HashMap<K, V> m = map;
      Node<K, V>[] tab = m.table;
      if ((hi = fence) < 0) {
        mc = expectedModCount = m.modCount;
        hi = fence = (tab == null) ? 0 : tab.length;
      } else {
        mc = expectedModCount;
      }
      if (tab != null && tab.length >= hi &&
          (i = index) >= 0 && (i < (index = hi) || current != null)) {
        Node<K, V> p = current;
        current = null;
        do {
          if (p == null) {
            p = tab[i++];
          } else {
            action.accept(p.value);
            p = p.next;
          }
        } while (p != null || i < hi);
        if (m.modCount != mc) {
          throw new ConcurrentModificationException();
        }
      }
    }

    public boolean tryAdvance(Consumer<? super V> action) {
      int hi;
      if (action == null) {
        throw new NullPointerException();
      }
      Node<K, V>[] tab = map.table;
      if (tab != null && tab.length >= (hi = getFence()) && index >= 0) {
        while (current != null || index < hi) {
          if (current == null) {
            current = tab[index++];
          } else {
            V v = current.value;
            current = current.next;
            action.accept(v);
            if (map.modCount != expectedModCount) {
              throw new ConcurrentModificationException();
            }
            return true;
          }
        }
      }
      return false;
    }

    public int characteristics() {
      return (fence < 0 || est == map.size ? Spliterator.SIZED : 0);
    }
  }

  static final class EntrySpliterator<K, V>
      extends HashMapSpliterator<K, V>
      implements Spliterator<Map.Entry<K, V>> {

    EntrySpliterator(HashMap<K, V> m, int origin, int fence, int est,
                     int expectedModCount) {
      super(m, origin, fence, est, expectedModCount);
    }

    public EntrySpliterator<K, V> trySplit() {
      int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
      return (lo >= mid || current != null) ? null :
          new EntrySpliterator<>(map, lo, index = mid, est >>>= 1,
              expectedModCount);
    }

    public void forEachRemaining(Consumer<? super Map.Entry<K, V>> action) {
      int i, hi, mc;
      if (action == null) {
        throw new NullPointerException();
      }
      HashMap<K, V> m = map;
      Node<K, V>[] tab = m.table;
      if ((hi = fence) < 0) {
        mc = expectedModCount = m.modCount;
        hi = fence = (tab == null) ? 0 : tab.length;
      } else {
        mc = expectedModCount;
      }
      if (tab != null && tab.length >= hi &&
          (i = index) >= 0 && (i < (index = hi) || current != null)) {
        Node<K, V> p = current;
        current = null;
        do {
          if (p == null) {
            p = tab[i++];
          } else {
            action.accept(p);
            p = p.next;
          }
        } while (p != null || i < hi);
        if (m.modCount != mc) {
          throw new ConcurrentModificationException();
        }
      }
    }

    public boolean tryAdvance(Consumer<? super Map.Entry<K, V>> action) {
      int hi;
      if (action == null) {
        throw new NullPointerException();
      }
      Node<K, V>[] tab = map.table;
      if (tab != null && tab.length >= (hi = getFence()) && index >= 0) {
        while (current != null || index < hi) {
          if (current == null) {
            current = tab[index++];
          } else {
            Node<K, V> e = current;
            current = current.next;
            action.accept(e);
            if (map.modCount != expectedModCount) {
              throw new ConcurrentModificationException();
            }
            return true;
          }
        }
      }
      return false;
    }

    public int characteristics() {
      return (fence < 0 || est == map.size ? Spliterator.SIZED : 0) |
          Spliterator.DISTINCT;
    }
  }

    /* ------------------------------------------------------------ */
  // LinkedHashMap support


    /*
     * The following package-protected methods are designed to be
     * overridden by LinkedHashMap, but not by any other subclass.
     * Nearly all other internal methods are also package-protected
     * but are declared final, so can be used by LinkedHashMap, view
     * classes, and HashSet.
     */

  // 创建一个链表结点
  // Create a regular (non-tree) node
  Node<K, V> newNode(int hash, K key, V value, Node<K, V> next) {
    return new Node<>(hash, key, value, next);
  }

  // 替换一个链表节点
  // For conversion from TreeNodes to plain nodes
  Node<K, V> replacementNode(Node<K, V> p, Node<K, V> next) {
    return new Node<>(p.hash, p.key, p.value, next);
  }

  // 创建一个红黑树节点
  // Create a tree bin node
  TreeNode<K, V> newTreeNode(int hash, K key, V value, Node<K, V> next) {
    return new TreeNode<>(hash, key, value, next);
  }


  // 替换一个红黑树节点
  // For treeifyBin
  TreeNode<K, V> replacementTreeNode(Node<K, V> p, Node<K, V> next) {
    return new TreeNode<>(p.hash, p.key, p.value, next);
  }

  /**
   * Reset to initial default state.  Called by clone and readObject.
   */
  void reinitialize() {
    table = null;
    entrySet = null;
    keySet = null;
    values = null;
    modCount = 0;
    threshold = 0;
    size = 0;
  }

  // Callbacks to allow LinkedHashMap post-actions
  void afterNodeAccess(Node<K, V> p) {
  }

  void afterNodeInsertion(boolean evict) {
  }

  void afterNodeRemoval(Node<K, V> p) {
  }

  // Called only from writeObject, to ensure compatible ordering.
  // 写入hashMap键值对到ObjectOutputStream中
  void internalWriteEntries(java.io.ObjectOutputStream s) throws IOException {
    Node<K, V>[] tab;
    if (size > 0 && (tab = table) != null) {
      for (int i = 0; i < tab.length; ++i) {
        for (Node<K, V> e = tab[i]; e != null; e = e.next) {
          s.writeObject(e.key);
          s.writeObject(e.value);
        }
      }
    }
  }

    /* ------------------------------------------------------------ */
  // Tree bins

  /**
   * Entry for Tree bins. Extends LinkedHashMap.Entry (which in turn
   * extends Node) so can be used as extension of either regular or
   * linked node.
   */
  /**
   * JDK1.8新增，用来支持桶的红黑树结构实现
   * 性质1. 节点是红色或黑色。
   * 性质2. 根是黑色。
   * 性质3. 所有叶子都是黑色（叶子是NIL节点）。
   * 性质4. 每个红色节点必须有两个黑色的子节点。(从每个叶子到根的所有路径上不能有两个连续的红色节点。)
   * 性质5. 从任一节点到其每个叶子的所有简单路径都包含相同数目的黑色节点。
   */
  static final class TreeNode<K, V> extends LinkedHashMap.Entry<K, V> {

    //父亲
    TreeNode<K, V> parent;  // red-black tree links
    //左右孩子
    TreeNode<K, V> left;
    TreeNode<K, V> right;
    // 前一个元素的节点
    TreeNode<K, V> prev;    // needed to unlink next upon deletion
    // 红黑节点标识
    boolean red;

    TreeNode(int hash, K key, V val, Node<K, V> next) {
      super(hash, key, val, next);
    }

    /**
     * Returns root of tree containing this node.
     */
    /**
     * 获取红黑树的根
     */
    final TreeNode<K, V> root() {
      for (TreeNode<K, V> r = this, p; ; ) {
        if ((p = r.parent) == null) {
          return r;
        }
        r = p;
      }
    }

    /**
     * Ensures that the given root is the first node of its bin.
     */
    /**
     * 确保root是桶中的第一个元素 ，将root移到中中的第一个
     */
    static <K, V> void moveRootToFront(Node<K, V>[] tab, TreeNode<K, V> root) {
      int n;
      if (root != null && tab != null && (n = tab.length) > 0) {
        int index = (n - 1) & root.hash;
        TreeNode<K, V> first = (TreeNode<K, V>) tab[index];
        if (root != first) {
          Node<K, V> rn;
          tab[index] = root;
          TreeNode<K, V> rp = root.prev;
          if ((rn = root.next) != null) {
            ((TreeNode<K, V>) rn).prev = rp;
          }
          if (rp != null) {
            rp.next = rn;
          }
          if (first != null) {
            first.prev = root;
          }
          root.next = first;
          root.prev = null;
        }
        assert checkInvariants(root);
      }
    }

    /**
     * Finds the node starting at root p with the given hash and key.
     * The kc argument caches comparableClassFor(key) upon first use
     * comparing keys.
     */
    /**
     * 查找hash为h，key为k的节点
     */
    final TreeNode<K, V> find(int h, Object k, Class<?> kc) {
      TreeNode<K, V> p = this;
      do {
        int ph, dir;
        K pk;
        TreeNode<K, V> pl = p.left, pr = p.right, q;
        if ((ph = p.hash) > h) {
          p = pl;
        } else if (ph < h) {
          p = pr;
        } else if ((pk = p.key) == k || (k != null && k.equals(pk))) {
          return p;
        } else if (pl == null) {
          p = pr;
        } else if (pr == null) {
          p = pl;
        } else if ((kc != null ||
            (kc = comparableClassFor(k)) != null) &&
            (dir = compareComparables(kc, k, pk)) != 0) {
          p = (dir < 0) ? pl : pr;
        } else if ((q = pr.find(h, k, kc)) != null) {
          return q;
        } else {
          p = pl;
        }
      } while (p != null);
      return null;
    }

    /**
     * Calls find for root node.
     */
    /**
     * 获取树节点，通过根节点查找
     */
    final TreeNode<K, V> getTreeNode(int h, Object k) {
      return ((parent != null) ? root() : this).find(h, k, null);
    }

    /**
     * Tie-breaking utility for ordering insertions when equal
     * hashCodes and non-comparable. We don't require a total
     * order, just a consistent insertion rule to maintain
     * equivalence across rebalancings. Tie-breaking further than
     * necessary simplifies testing a bit.
     */
    /**
     * 比较2个对象的大小
     */
    static int tieBreakOrder(Object a, Object b) {
      int d;
      if (a == null || b == null ||
          (d = a.getClass().getName().
              compareTo(b.getClass().getName())) == 0) {
        d = (System.identityHashCode(a) <= System.identityHashCode(b) ?
            -1 : 1);
      }
      return d;
    }

    /**
     * Forms tree of the nodes linked from this node.
     *
     * @return root of tree
     */
    /**
     * 将链表转为二叉树
     *
     * @return root of tree
     */
    final void treeify(Node<K, V>[] tab) {
      //root节点
      TreeNode<K, V> root = null;
      for (TreeNode<K, V> x = this, next; x != null; x = next) {
        //next　下一个节点
        next = (TreeNode<K, V>) x.next;
        //设置左右节点为空
        x.left = x.right = null;

        // 头回进入循环　root　== null，确定头结点，为黑色
        if (root == null) {
          // 将根节点的父节点设置位空
          x.parent = null;
          // 将根节点设置为 black
          x.red = false;
          //将x 设置为根节点
          root = x;

        // 后面进入循环走的逻辑，x 指向树中的某个节点。 此处为非根节点
        } else {
          //　获取当前循环节点key
          K k = x.key;
          // 获取当前节点 hash
          int h = x.hash;
          Class<?> kc = null;

          // 从根节点开始验证，遍历所有节点跟当前节点 x 比较，调整位置，有点像冒泡排序
          for (TreeNode<K, V> p = root; ; ) {
            int dir, ph;
            // 每个节点的 key
            K pk = p.key;

            // 每个节点的ｈａｓh 与　外层循环的ｘ.hash做比较. 当比较节点的哈希值比 x 大时， dir 为 -1
            if ((ph = p.hash) > h) {
              // <0 ,沿左路径查找
              dir = -1;

            // >0, 沿右路径查找
            } else if (ph < h) {
              dir = 1;

            // 如果存在比较对象，则根据比较对象定义的comparable进行比较
            // 比较之后返回查询节点路径（左或右）
            } else if ((kc == null &&
                (kc = comparableClassFor(k)) == null) ||
                (dir = compareComparables(kc, k, pk)) == 0) {
              dir = tieBreakOrder(k, pk);
            }

            // 如果当前比较节点的哈希值比 x 大，x 就是左孩子，否则 x 是右孩子
            TreeNode<K, V> xp = p;

            //　如果父节点的左节点或右节点为空时，才进行插入操作
            if ((p = (dir <= 0) ? p.left : p.right) == null) {
              // 将px设置为ｘ的父节点
              x.parent = xp;
              if (dir <= 0) {
                xp.left = x;
              } else {
                xp.right = x;
              }

              // 将二叉树转换位红黑树－正式转换红黑树
              root = balanceInsertion(root, x);
              break;
            }
          }
        }
      }
      moveRootToFront(tab, root);
    }

    /**
     * Returns a list of non-TreeNodes replacing those linked from
     * this node.
     */
    /**
     * 将二叉树转为链表
     */
    final Node<K, V> untreeify(HashMap<K, V> map) {
      Node<K, V> hd = null, tl = null;
      for (Node<K, V> q = this; q != null; q = q.next) {
        Node<K, V> p = map.replacementNode(q, null);
        if (tl == null) {
          hd = p;
        } else {
          tl.next = p;
        }
        tl = p;
      }
      return hd;
    }

    /**
     * Tree version of putVal.
     */
    /**
     * 添加一个键值对
     */
    final TreeNode<K, V> putTreeVal(HashMap<K, V> map, Node<K, V>[] tab,
                                    int h, K k, V v) {
      Class<?> kc = null;
      boolean searched = false;
      TreeNode<K, V> root = (parent != null) ? root() : this;
      for (TreeNode<K, V> p = root; ; ) {
        int dir, ph;
        K pk;
        if ((ph = p.hash) > h) {
          dir = -1;
        } else if (ph < h) {
          dir = 1;
        } else if ((pk = p.key) == k || (k != null && k.equals(pk))) {
          return p;
        } else if ((kc == null &&
            (kc = comparableClassFor(k)) == null) ||
            (dir = compareComparables(kc, k, pk)) == 0) {
          if (!searched) {
            TreeNode<K, V> q, ch;
            searched = true;
            if (((ch = p.left) != null &&
                (q = ch.find(h, k, kc)) != null) ||
                ((ch = p.right) != null &&
                    (q = ch.find(h, k, kc)) != null)) {
              return q;
            }
          }
          dir = tieBreakOrder(k, pk);
        }

        TreeNode<K, V> xp = p;
        if ((p = (dir <= 0) ? p.left : p.right) == null) {
          Node<K, V> xpn = xp.next;
          TreeNode<K, V> x = map.newTreeNode(h, k, v, xpn);
          if (dir <= 0) {
            xp.left = x;
          } else {
            xp.right = x;
          }
          xp.next = x;
          x.parent = x.prev = xp;
          if (xpn != null) {
            ((TreeNode<K, V>) xpn).prev = x;
          }
          moveRootToFront(tab, balanceInsertion(root, x));
          return null;
        }
      }
    }

    /**
     * Removes the given node, that must be present before this call.
     * This is messier than typical red-black deletion code because we
     * cannot swap the contents of an interior node with a leaf
     * successor that is pinned by "next" pointers that are accessible
     * independently during traversal. So instead we swap the tree
     * linkages. If the current tree appears to have too few nodes,
     * the bin is converted back to a plain bin. (The test triggers
     * somewhere between 2 and 6 nodes, depending on tree structure).
     */
    final void removeTreeNode(HashMap<K, V> map, Node<K, V>[] tab,
                              boolean movable) {
      int n;
      if (tab == null || (n = tab.length) == 0) {
        return;
      }
      int index = (n - 1) & hash;
      TreeNode<K, V> first = (TreeNode<K, V>) tab[index], root = first, rl;
      TreeNode<K, V> succ = (TreeNode<K, V>) next, pred = prev;
      if (pred == null) {
        tab[index] = first = succ;
      } else {
        pred.next = succ;
      }
      if (succ != null) {
        succ.prev = pred;
      }
      if (first == null) {
        return;
      }
      if (root.parent != null) {
        root = root.root();
      }
      if (root == null || root.right == null ||
          (rl = root.left) == null || rl.left == null) {
        tab[index] = first.untreeify(map);  // too small
        return;
      }
      TreeNode<K, V> p = this, pl = left, pr = right, replacement;
      if (pl != null && pr != null) {
        TreeNode<K, V> s = pr, sl;
        while ((sl = s.left) != null) // find successor
        {
          s = sl;
        }
        boolean c = s.red;
        s.red = p.red;
        p.red = c; // swap colors
        TreeNode<K, V> sr = s.right;
        TreeNode<K, V> pp = p.parent;
        if (s == pr) { // p was s's direct parent
          p.parent = s;
          s.right = p;
        } else {
          TreeNode<K, V> sp = s.parent;
          if ((p.parent = sp) != null) {
            if (s == sp.left) {
              sp.left = p;
            } else {
              sp.right = p;
            }
          }
          if ((s.right = pr) != null) {
            pr.parent = s;
          }
        }
        p.left = null;
        if ((p.right = sr) != null) {
          sr.parent = p;
        }
        if ((s.left = pl) != null) {
          pl.parent = s;
        }
        if ((s.parent = pp) == null) {
          root = s;
        } else if (p == pp.left) {
          pp.left = s;
        } else {
          pp.right = s;
        }
        if (sr != null) {
          replacement = sr;
        } else {
          replacement = p;
        }
      } else if (pl != null) {
        replacement = pl;
      } else if (pr != null) {
        replacement = pr;
      } else {
        replacement = p;
      }
      if (replacement != p) {
        TreeNode<K, V> pp = replacement.parent = p.parent;
        if (pp == null) {
          root = replacement;
        } else if (p == pp.left) {
          pp.left = replacement;
        } else {
          pp.right = replacement;
        }
        p.left = p.right = p.parent = null;
      }

      TreeNode<K, V> r = p.red ? root : balanceDeletion(root, replacement);

      if (replacement == p) {  // detach
        TreeNode<K, V> pp = p.parent;
        p.parent = null;
        if (pp != null) {
          if (p == pp.left) {
            pp.left = null;
          } else if (p == pp.right) {
            pp.right = null;
          }
        }
      }
      if (movable) {
        moveRootToFront(tab, r);
      }
    }

    /**
     * 将结点太多的桶分割
     *
     * Splits nodes in a tree bin into lower and upper tree bins, or untreeifies if now too small.
     * Called only from resize; see above discussion about split bits and indices.
     *
     * @param map the map
     * @param tab the table for recording bin heads 表示保存桶头结点的哈希表
     * @param index the index of the table being split 示从哪个位置开始修剪
     * @param bit the bit of hash to split on 要修剪的位数（哈希值）
     */
    final void split(HashMap<K, V> map, Node<K, V>[] tab, int index, int bit) {
      TreeNode<K, V> b = this;
      // Relink into lo and hi lists, preserving order
      TreeNode<K, V> loHead = null, loTail = null;
      TreeNode<K, V> hiHead = null, hiTail = null;
      int lc = 0, hc = 0;
      for (TreeNode<K, V> e = b, next; e != null; e = next) {
        next = (TreeNode<K, V>) e.next;
        e.next = null;

        // 如果当前节点哈希值的最后一位等于要修剪的 bit 值
        if ((e.hash & bit) == 0) {
          // 就把当前节点放到 lXXX 树中
          if ((e.prev = loTail) == null) {
            loHead = e;
          } else {
            loTail.next = e;
          }

          // 然后 loTail 记录 e
          loTail = e;
          // 记录 lXXX 树的节点数量
          ++lc;
        } else { // 如果当前节点哈希值最后一位不是要修剪的，就把当前节点放到 hXXX 树中
          if ((e.prev = hiTail) == null) {
            hiHead = e;
          } else {
            hiTail.next = e;
          }
          hiTail = e;
          // 记录 hXXX 树的节点数量
          ++hc;
        }
      }

      if (loHead != null) {
        // 如果 lXXX 树的数量小于 6，就把 lXXX 树的枝枝叶叶都置为空，变成一个单节点。
        // 然后让这个桶中，要还原索引位置开始往后的结点都变成还原成链表的 lXXX 节点。
        // 这一段元素以后就是一个链表结构
        if (lc <= UNTREEIFY_THRESHOLD) {
          tab[index] = loHead.untreeify(map);
        } else {
          // 否则让索引位置的结点指向 lXXX 树，这个树被修剪过，元素少了
          tab[index] = loHead;
          if (hiHead != null) // (else is already treeified)
          {
            loHead.treeify(tab);
          }
        }
      }
      if (hiHead != null) {
        // 同理，让 指定位置 index + bit 之后的元素
        // 指向 hXXX 还原成链表或者修剪过的树
        if (hc <= UNTREEIFY_THRESHOLD) {
          tab[index + bit] = hiHead.untreeify(map);
        } else {
          tab[index + bit] = hiHead;
          if (loHead != null) {
            hiHead.treeify(tab);
          }
        }
      }
    }

        /* ------------------------------------------------------------ */
    // Red-black tree methods, all adapted from CLR
    // 红黑树方法，都是从CLR中修改的

    // 左旋转
    static <K, V> TreeNode<K, V> rotateLeft(TreeNode<K, V> root,
                                            TreeNode<K, V> p) {
      TreeNode<K, V> r, pp, rl;
      if (p != null && (r = p.right) != null) {
        if ((rl = p.right = r.left) != null) {
          rl.parent = p;
        }
        if ((pp = r.parent = p.parent) == null) {
          (root = r).red = false;
        } else if (pp.left == p) {
          pp.left = r;
        } else {
          pp.right = r;
        }
        r.left = p;
        p.parent = r;
      }
      return root;
    }

    /**
     * 右旋转
     *
     * @param root 默认调用此方法前指定的root节点
     * @param p root的父节点
     *
     * @param <K>
     * @param <V>
     * @return
     */
    static <K, V> TreeNode<K, V> rotateRight(TreeNode<K, V> root,
                                             TreeNode<K, V> p) {
      // l: p的左节点 　pp:p的父节点　lr:p的左右节点
      TreeNode<K, V> l, pp, lr;

      // 传入参数
      // root: 默认调用此方法前指定的root节点
      // p: root的父节点
      if (p != null && (l = p.left) != null) {
        if ((lr = p.left = l.right) != null) {
          lr.parent = p;
        }

        // 判断p的父节点是否为空
        if ((pp = l.parent = p.parent) == null) {
          // 调整root的值
          (root = l).red = false;
        } else if (pp.right == p) {
          pp.right = l;
        } else {
          pp.left = l;
        }

        //　将p调整为　root 节点的右节点
        l.right = p;
        //将l调整为p的parent
        p.parent = l;
      }
      return root;
    }

    /**
     * 转换二叉树为红黑树， 保证插入后平衡
     * @param root　根节点
     * @param x　执行的节点
     *
     * @param <K>
     * @param <V>
     * @return
     */
    static <K, V> TreeNode<K, V> balanceInsertion(TreeNode<K, V> root,
                                                  TreeNode<K, V> x) {
      // 默认ｘ节点为红色节点
      x.red = true;

      /**
       * xp: 　　x的父节点
       * xpp: 　x父节点的父节点
       * xppl:　x父节点的父节点左子节点
       * xppr:　x父节点的父节点右子节点
       */
      for (TreeNode<K, V> xp, xpp, xppl, xppr; ; ) {
        // xp = x.parent
        // 如果x存在父节点，则说明目前只有一个节点,即root.根据红黑树的五大特征，根节点只能为黑色节点
        if ((xp = x.parent) == null) {
          x.red = false;
          return x;

        //xpp = xp.parent, 直接查询的是根节点
        } else if (!xp.red || (xpp = xp.parent) == null) {
          return root;
        }

        // xppl = xpp.left.  x的父节点是左节点时
        if (xp == (xppl = xpp.left)) {

          // 验证是否需要旋转
          // xppr = xpp.right 存在右节点　且　右节点为红色
          if ((xppr = xpp.right) != null && xppr.red) {
            xppr.red = false;// xppr　设置位ｂlack
            xp.red = false;  // xp　设置位ｂlack
            xpp.red = true;  // xpp　设置位red

            // 将x赋值为父节点的父节点
            x = xpp;
          } else {
            if (x == xp.right) {

              // 左旋转
              root = rotateLeft(root, x = xp);
              xpp = (xp = x.parent) == null ? null : xp.parent;
            }
            if (xp != null) {
              xp.red = false;
              if (xpp != null) {
                xpp.red = true;

                // 右旋转
                root = rotateRight(root, xpp);
              }
            }
          }

        } else {
          // 验证是否需要旋转
          if (xppl != null && xppl.red) {
            xppl.red = false;
            xp.red = false;
            xpp.red = true;
            x = xpp;
          } else {
            if (x == xp.left) {
              root = rotateRight(root, x = xp);
              xpp = (xp = x.parent) == null ? null : xp.parent;
            }
            if (xp != null) {
              xp.red = false;
              if (xpp != null) {
                xpp.red = true;
                root = rotateLeft(root, xpp);
              }
            }
          }
        }
      }
    }

    /**
     * 删除后调整平衡
     * @param root
     * @param x
     * @param <K>
     * @param <V>
     * @return
     */
    static <K, V> TreeNode<K, V> balanceDeletion(TreeNode<K, V> root,
                                                 TreeNode<K, V> x) {
      for (TreeNode<K, V> xp, xpl, xpr; ; ) {
        if (x == null || x == root) {
          return root;
        } else if ((xp = x.parent) == null) {
          x.red = false;
          return x;
        } else if (x.red) {
          x.red = false;
          return root;
        } else if ((xpl = xp.left) == x) {
          if ((xpr = xp.right) != null && xpr.red) {
            xpr.red = false;
            xp.red = true;
            root = rotateLeft(root, xp);
            xpr = (xp = x.parent) == null ? null : xp.right;
          }
          if (xpr == null) {
            x = xp;
          } else {
            TreeNode<K, V> sl = xpr.left, sr = xpr.right;
            if ((sr == null || !sr.red) &&
                (sl == null || !sl.red)) {
              xpr.red = true;
              x = xp;
            } else {
              if (sr == null || !sr.red) {
                if (sl != null) {
                  sl.red = false;
                }
                xpr.red = true;
                root = rotateRight(root, xpr);
                xpr = (xp = x.parent) == null ?
                    null : xp.right;
              }
              if (xpr != null) {
                xpr.red = (xp == null) ? false : xp.red;
                if ((sr = xpr.right) != null) {
                  sr.red = false;
                }
              }
              if (xp != null) {
                xp.red = false;
                root = rotateLeft(root, xp);
              }
              x = root;
            }
          }
        } else { // symmetric
          if (xpl != null && xpl.red) {
            xpl.red = false;
            xp.red = true;
            root = rotateRight(root, xp);
            xpl = (xp = x.parent) == null ? null : xp.left;
          }
          if (xpl == null) {
            x = xp;
          } else {
            TreeNode<K, V> sl = xpl.left, sr = xpl.right;
            if ((sl == null || !sl.red) &&
                (sr == null || !sr.red)) {
              xpl.red = true;
              x = xp;
            } else {
              if (sl == null || !sl.red) {
                if (sr != null) {
                  sr.red = false;
                }
                xpl.red = true;
                root = rotateLeft(root, xpl);
                xpl = (xp = x.parent) == null ?
                    null : xp.left;
              }
              if (xpl != null) {
                xpl.red = (xp == null) ? false : xp.red;
                if ((sl = xpl.left) != null) {
                  sl.red = false;
                }
              }
              if (xp != null) {
                xp.red = false;
                root = rotateRight(root, xp);
              }
              x = root;
            }
          }
        }
      }
    }

    /**
     * Recursive invariant check
     */
    /**
     * 检测是否符合红黑树
     */
    static <K, V> boolean checkInvariants(TreeNode<K, V> t) {
      TreeNode<K, V> tp = t.parent, tl = t.left, tr = t.right,
          tb = t.prev, tn = (TreeNode<K, V>) t.next;
      if (tb != null && tb.next != t) {
        return false;
      }
      if (tn != null && tn.prev != t) {
        return false;
      }
      if (tp != null && t != tp.left && t != tp.right) {
        return false;
      }
      if (tl != null && (tl.parent != t || tl.hash > t.hash)) {
        return false;
      }
      if (tr != null && (tr.parent != t || tr.hash < t.hash)) {
        return false;
      }
      if (t.red && tl != null && tl.red && tr != null && tr.red) {
        return false;
      }
      if (tl != null && !checkInvariants(tl)) {
        return false;
      }
      if (tr != null && !checkInvariants(tr)) {
        return false;
      }
      return true;
    }
  }

}
