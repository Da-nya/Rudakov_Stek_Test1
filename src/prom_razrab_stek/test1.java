//Рудаков Даниил
//ЯрГУ ИВТ-42
package prom_razrab_stek;

import java.util.*;

class TestRunner {
   TestRunner(String name) {
      this.name = name;
   }

   interface BooleanTestCase {
      boolean run();
   }

   void expectTrue(BooleanTestCase cond) {
      try {
         if (cond.run()) {
            pass();
         }
         else {
            fail();
         }
      }
      catch (Exception e) {
         fail(e);
      }
   }

   void expectFalse(BooleanTestCase cond) {
      expectTrue(() -> !cond.run());
   }

   interface ThrowingTestCase {
      void run();
   }

   void expectException(ThrowingTestCase block) {
      try {
         block.run();
         fail();
      }
      catch (Exception e) {
         pass();
      }
   }

   private void fail() {
      System.out.printf("FAILED: Test %d of %s\n", testNo++, name);
   }

   private void fail(Exception e) {
      fail();
      e.printStackTrace(System.out);
   }

   private void pass() {
      System.out.printf("PASSED: Test %d of %s\n", testNo++, name);
   }

   private String name;
   private int testNo = 1;
}


class Matcher {
   static boolean match(String string, String pattern) {
   // ------------------------------------------------------------------------------------------------
   // Решение задачи 1
   // ------------------------------------------------------------------------------------------------
	   boolean answer = true;//false - если появилось различие с шаблоном
	   int code_a = (int)'a';
	   int code_z = (int)'z';
	   
	   if (string.length()==pattern.length())
		   for(int i=0;i<string.length();i++)
		   {
			   if (pattern.charAt(i) == 'a')
			   {
				   int code_str = (int)string.charAt(i);
				   answer &=  (code_a <= code_str) && (code_str <= code_z); //проверяем по коду символа, является ли он буквой
			   }
			   else 
				   if (pattern.charAt(i) == 'd')
					   answer &= Character.isDigit(string.charAt(i));//проверяем, является ли символ цифрой
				   else 
					   if (pattern.charAt(i) == ' ')
						   answer &= (string.charAt(i) == ' ');
					   else 
						   if (pattern.charAt(i) == '*')
						   {
							   int code_str = (int)string.charAt(i);
							   boolean is_my_letter = (code_a <= code_str) && (code_str <= code_z);
							   answer &= Character.isDigit(string.charAt(i)) || is_my_letter;//проверка - число или буква
						   }
						   else throw new RuntimeException();//исключение - ошибка в шаблоне
		   }
	   else answer = false;
	   return answer;
   }

   static void testMatch() {
      TestRunner runner = new TestRunner("match");
      
      runner.expectFalse(() -> match("xy", "a"));
      runner.expectFalse(() -> match("x", "d"));
      runner.expectFalse(() -> match("0", "a"));
      runner.expectFalse(() -> match("*", " "));
      runner.expectFalse(() -> match(" ", "a"));

      runner.expectTrue(() -> match("01 xy", "dd aa"));
      runner.expectTrue(() -> match("1x", "**"));

      runner.expectException(() -> {
         match("x", "w");
      });
   }
}


class TaskFinder {
   static class Node {
      Node(int id, String name, Integer priority, List<Node> children) {
         this.id = id;
         this.name = name;
         this.priority = priority;
         this.children = children;
      }

      boolean isGroup() {
         return children != null;
      }

      int id;
      String name;
      Integer priority;
      List<Node> children;

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         }
         if (o == null || getClass() != o.getClass()) {
            return false;
         }
         Node node = (Node) o;
         return id == node.id
               && name.equals(node.name)
               && Objects.equals(priority, node.priority)
               && Objects.equals(children, node.children);
      }

      @Override
      public int hashCode() {
         return Objects.hash(id, name, priority, children);
      }
   }

   static Node task(int id, String name, int priority) {
      return new Node(id, name, priority, null);
   }

   static  Node group(int id, String name, Node... children) {
      return new Node(id, name, null, Arrays.asList(children));
   }


   static Node tasks =
         group(0, "Все задачи",
            group(1, "Разработка",
               task(2, "Планирование разработок", 1),
               task(3, "Подготовка релиза", 4),
               task(4, "Оптимизация", 2)),
            group(5, "Тестирование",
               group(6, "Ручное тестирование",
                  task(7, "Составление тест-планов", 3),
                  task(8, "Выполнение тестов", 6)),
               group(9, "Автоматическое тестирование",
                  task(10, "Составление тест-планов", 3),
                  task(11, "Написание тестов", 3))),
            group(12, "Аналитика"));
   
   // ------------------------------------------------------------------------------------------------
   // Решение задачи 2
   
   // находит номер группы по заданному id
   static Node find_group(List<Node> childs, int id)
   {
	   Node t = null;
	   for (int i=0; i < childs.size();i++)
	   {
		   if (childs.get(i).id == id)
			   t = childs.get(i);
		   else
			   if (childs.get(i).isGroup())
				  t = find_group(childs.get(i).children,id);
				   
		   if (t!=null)
			   return t;
	   }
	   return t;
   }
   
   //находим задачу с наибольшим приоритетом
   static Node find_maxPriority(List<Node> childs)
   {
	Node t=null;
	for (int i=0;i<childs.size();i++)
		if (childs.get(i).isGroup())
		{
			Node p = find_maxPriority(childs.get(i).children);
			if (t==null)
				t=p;
			else 
				if (p!=null)
					if(t.priority<p.priority)
						t=p;
		}
		else 
			if(t==null)
				t=childs.get(i);
			else 
				if (t.priority<childs.get(i).priority)
					t=childs.get(i);
	return t;
   }
   static Optional<Node> findTaskHavingMaxPriorityInGroup(Node tasks, int groupId) {
   // ------------------------------------------------------------------------------------------------
   // Решение задачи 2
   // ------------------------------------------------------------------------------------------------
	   Optional<Node> answer=Optional.empty();
	   Node t;
	   //t= Optional.of(find_group(tasks.children,groupId));
	   if (tasks.id == groupId)
		   t=tasks;
	   else t = find_group(tasks.children,groupId);
	   if (t==null)
		  throw new RuntimeException();//исключение - такого id не существует 
	   else
		   if (!t.isGroup())
			   throw new RuntimeException();//исключение - не является группой
		   else
			   answer = Optional.ofNullable(find_maxPriority(t.children));  
	   return answer;
   }


   static void testFindTaskHavingMaxPriorityInGroup() {
      TestRunner runner = new TestRunner("findTaskHavingMaxPriorityInGroup");

      runner.expectException(() -> findTaskHavingMaxPriorityInGroup(tasks, 13));
      runner.expectException(() -> findTaskHavingMaxPriorityInGroup(tasks, 2));

      runner.expectFalse(() -> findTaskHavingMaxPriorityInGroup(tasks, 12).isPresent());

      runner.expectTrue(() -> findTaskHavingMaxPriorityInGroup(tasks, 0).get()
            .equals(task(8, "Выполнение тестов", 6)));
      runner.expectTrue(() -> findTaskHavingMaxPriorityInGroup(tasks, 1).get()
            .equals(task(3, "Подготовка релиза", 4)));

      runner.expectTrue(() -> findTaskHavingMaxPriorityInGroup(tasks, 9).get().priority == 3);
   }
}


class Main {
   public static void main(String args[]) {
      Matcher.testMatch();
      TaskFinder.testFindTaskHavingMaxPriorityInGroup();
   }
}