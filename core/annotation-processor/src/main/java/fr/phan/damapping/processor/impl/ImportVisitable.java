package fr.phan.damapping.processor.impl;

/**
* ImportVisitable -
*
* @author Sébastien Lesaint
*/
interface ImportVisitable {
    void visite(ImportVisitor visitor);
}
