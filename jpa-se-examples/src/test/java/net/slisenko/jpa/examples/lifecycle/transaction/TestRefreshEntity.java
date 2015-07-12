package net.slisenko.jpa.examples.lifecycle.transaction;

/**
 * TODO test REFRESH - insert few entities, then make bulk JPQL update (which does not update cache), then refresh entities
 * TODO another case - undo changes, made in current transaction - revert them back
 * TODO implement cascade refresh
 * REFRESH - we have changes in database and we want to copy this changed to entity (only for managed entities)
 */
public class TestRefreshEntity {
}