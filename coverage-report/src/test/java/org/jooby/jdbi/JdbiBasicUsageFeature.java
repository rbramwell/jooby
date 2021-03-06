package org.jooby.jdbi;

import org.jooby.test.ServerFeature;
import org.junit.Test;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.util.StringColumnMapper;

import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;

public class JdbiBasicUsageFeature extends ServerFeature {

  {

    use(ConfigFactory.empty().withValue("db", ConfigValueFactory.fromAnyRef("mem")));

    use(new Jdbi());

    get("/jdbi-handle", req -> {
      try (Handle h = req.require(Handle.class)) {
        h.execute("create table something (id int primary key, name varchar(100))");

        h.execute("insert into something (id, name) values (?, ?)", 1, "Jooby");

        String name = h.createQuery("select name from something where id = :id")
            .bind("id", 1)
            .map(StringColumnMapper.INSTANCE)
            .first();

        return name;
      }
    });
  }

  @Test
  public void doWithHandle() throws Exception {
    request()
        .get("/jdbi-handle")
        .expect("Jooby");
  }
}
